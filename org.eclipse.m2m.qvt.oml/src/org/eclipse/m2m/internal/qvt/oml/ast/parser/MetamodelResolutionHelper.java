/*******************************************************************************
 * Copyright (c) 2008, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.ocl.ecore.EcoreEnvironment;

/**
 * FIXME - this class extracts the logic of resolving workspace located metamodels, 
 * formerly injected into QVT Environments implementation.
 * A temporary workaround, to be revisited.
 */
class MetamodelResolutionHelper {
	private static final Logger LOGGER = Logger.getLogger( MetamodelResolutionHelper.class.getName() );

	/**
	 * Registers metamodel for use with this environment.
	 * 
	 * @return the metamodel package denoted by the given <code>URI</code> or
	 *         <code>null</code> if no package was resolved
	 */
	static List<EPackage> registerMetamodel(QvtOperationalEnv qvtEnv, String metamodelUri, List<String> path) {
		EPackage.Registry registry = qvtEnv.getFactory().getEPackageRegistry();
        List<EPackage> metamodels = new ArrayList<EPackage>(1);
        
		
	    List<EPackage> desc = Collections.emptyList();            
        if (metamodelUri != null && path.isEmpty()) {
            EPackage ePackage = registry.getEPackage(metamodelUri);
            if(ePackage != null) {                	
            	desc = Collections.singletonList(ePackage);
            } else {
            	ePackage = tryLookupEmptyRootPackage(metamodelUri, registry);
            	if(ePackage != null) {                	
                	desc = Collections.singletonList(ePackage);
                }
           }
        } else {
            desc = resolveMetamodels(registry, path);
        }
                    
		for(EPackage model : desc) {							        	
            // register meta-model for EClassifier lookup
        	if (model.getNsURI() == null) {
				model = EmfUtil.getRootPackage(model);
        	}
        	
        	metamodels.add(model);
        	if(metamodelUri != null) {
        		qvtEnv.getEPackageRegistry().put(metamodelUri, model);
        	}
        }
		
		return metamodels;
	}	
	
	public static EPackage tryLookupEmptyRootPackage(String nsURI, EPackage.Registry registry) {
		URI rootURI = URI.createURI(nsURI);
		if(rootURI.segmentCount() == 0) {
			return null;
		}
		
		String base = rootURI.segment(0);
		String commonBaseURI = rootURI.trimSegments(rootURI.segmentCount()).appendSegment(base).toString().toLowerCase();
		
		LinkedList<String> candidates = new LinkedList<String>();
		for (String nextURI : registry.keySet()) {
			if(nextURI.toLowerCase().startsWith(commonBaseURI)) {
				candidates.add(nextURI);
			}
		}
				
		// first attempt to select few packages which are likely
		// to be child packages to avoid initialization of all packages
		// in the registry
        for(String nextNsURI : candidates) {
        	EPackage pack = null;
        	
        	try {
        		pack = registry.getEPackage(nextNsURI);
        	}
        	catch(Throwable t) {
        		LOGGER.log(Level.SEVERE,t, () -> "Error getting EPackage "+nextNsURI);
//        		EmfUtilPlugin.log(t);
        	}
        	
        	if (pack != null) {
	        	pack = EmfUtil.getRootPackage(pack);
	        	
	        	if (nsURI.equals(pack.getNsURI())) {
	        		return pack;
	        	}
        	}
        }

        // too greedy to check all packages
//        // check all packages in the registry
//        for(String nextNsURI : registry.keySet()) {
//        	EPackage pack = registry.getEPackage(nextNsURI);
//
//        	while (pack.getESuperPackage() != null) {
//    			pack = pack.getESuperPackage();
//    		}
//        	
//        	if (nsURI.equals(pack.getNsURI())) {
//        		return pack;
//        	}
//        }
        
		return null;
	}
	public static List<EPackage> resolveMetamodels(EPackage.Registry registry, List<String> packageName) {	
		final List<EPackage> metamodels = new UniqueEList<EPackage>(findPackages(registry, packageName));
		
		if (metamodels.isEmpty()) {
			metamodels.addAll(resolveUrilessMetamodels(registry, packageName));
		}

        return metamodels; 
	}
	private static List<EPackage> findPackages(EPackage.Registry registry, List<String> packageName) {
    	final List<EPackage> metamodels = new UniqueEList<EPackage>(1);
		
    	// do not iterate over the key set itself to avoid concurrent modification
    	final List<String> keys = new ArrayList<String>(registry.keySet());
        for (String nsURI : keys) {
        	EPackage pack = null;
        	
        	try {
        		pack = registry.getEPackage(nsURI);
        	}
        	catch(Throwable t) {
        		LOGGER.log(Level.SEVERE,t, () -> "Error getting EPackage "+nsURI);
//        		EmfUtilPlugin.log(t);
        	}
        	
        	if (pack == null || pack.getESuperPackage() != null) {
        		continue;
        	}
        	EPackage lookupPackage = lookupPackage(pack, packageName);
        	if (lookupPackage != null) {
        		metamodels.add(lookupPackage);
        	}
        }
                
        return metamodels;
    }
	
	public static EPackage lookupPackage(EPackage rootPackage, List<String> path) {
		EPackage.Registry registry = new EPackageRegistryImpl();
		registry.put(rootPackage.getNsURI(), rootPackage);
		
		return EcoreEnvironment.findPackage(path, registry);
	}
	
	/**
	 * Special case for uriless ModelType registration like
	 * 'modeltype Ecore uses ecore;'
	 */
	private static List<EPackage> resolveUrilessMetamodels(EPackage.Registry registry, List<String> packageName) {	
		final List<EPackage> metamodels = new UniqueEList<EPackage>(1);
		
		for(EPackage globalPack : findPackages(EPackage.Registry.INSTANCE, packageName)) {
			
			// check if this global package is a valid finding
			// i.e. if its root package is actually registered in the given registry
			EPackage rootPack = EmfUtil.getRootPackage(globalPack);
    		EPackage registeredPack = null;
    		
    		try {
    			registeredPack = registry.getEPackage(rootPack.getNsURI());
    		}
    		catch(Throwable t) {
        		LOGGER.log(Level.SEVERE,t, () -> "Error getting EPackage "+rootPack.getNsURI());
//				EmfUtilPlugin.log(t);
    		}
    		
			if(registeredPack == rootPack) {
    			metamodels.add(globalPack);
    		}
		}
      
        return metamodels; 
	}
}
