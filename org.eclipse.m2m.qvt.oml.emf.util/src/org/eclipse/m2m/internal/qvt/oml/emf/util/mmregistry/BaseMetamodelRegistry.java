/*******************************************************************************
 * Copyright (c) 2007, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 428620
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfException;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.component.annotations.Component;


/** @author pkobiakov */
@Component(service = IMetamodelRegistry.class)
public class BaseMetamodelRegistry implements IMetamodelRegistry {
    
    public static boolean isMetamodelFileName(String fileName) {
    	return fileName.endsWith(".ecore") //$NON-NLS-1$
	        || fileName.endsWith(".xcore") //$NON-NLS-1$
	        || fileName.endsWith(".emof") //$NON-NLS-1$
	        || fileName.endsWith(".oclinecore") //$NON-NLS-1$
	        ;
    }
	
	
    public static final String MM_POINT_ID = "metamodelProvider"; //$NON-NLS-1$
    
    private static final BaseMetamodelRegistry ourInstance = new BaseMetamodelRegistry();
		
	private final IMetamodelProvider myMetamodelProvider;
	
	
	private BaseMetamodelRegistry() {	
		this(getDefaultMetamodelProvider());
    }
	
	public BaseMetamodelRegistry(EPackage.Registry packageRegistry) {
		this(getDefaultMetamodelProvider(packageRegistry));
	}
		
	public BaseMetamodelRegistry(IMetamodelProvider metamodelProvider) {				
		myMetamodelProvider = metamodelProvider;
	}
	
    public EPackage.Registry toEPackageRegistry() {
    	return myMetamodelProvider.getPackageRegistry();
    }
    
    public static IMetamodelProvider getDefaultMetamodelProvider() {
		return getDefaultMetamodelProvider(EPackage.Registry.INSTANCE);
	}
    
    public static IMetamodelProvider getDefaultMetamodelProvider(EPackage.Registry packageRegistry) {
		return getDefaultMetamodelProvider(new EmfStandaloneMetamodelProvider(packageRegistry));
	}
	
	public static IMetamodelProvider getDefaultMetamodelProvider(IMetamodelProvider base) {
		return base;
	}	
    
    public static BaseMetamodelRegistry getInstance() {
        return ourInstance;
    }
        
	public String[] getMetamodelIds() {
		IMetamodelDesc[] metamodels = myMetamodelProvider.getMetamodels();
		
		final Set<String> ids = new LinkedHashSet<String>(metamodels.length);		
		for(IMetamodelDesc desc : metamodels) {
			ids.add(desc.getId());
		}
		
		return ids.toArray(new String[ids.size()]);
	}

	
	
	public IMetamodelDesc getMetamodelDesc(String id) throws EmfException {
		IMetamodelDesc desc = myMetamodelProvider.getMetamodel(id);
		
		// FIXME - hack for #35157 
		if(desc == null && id != null) {
            for(IMetamodelDesc d: myMetamodelProvider.getMetamodels()) {
            	EPackage pack = d.getModel();
            	if (pack == null) {
            		continue;
            	}
            	if (id.equals(pack.getNsURI())) {
            		desc = d;
            		break;
            	}
        		pack = EmfUtil.getRootPackage(pack);
            	if (id.equals(pack.getNsURI())) {
            		desc = new EmfMetamodelDesc(pack, pack.getNsURI());
            		break;
            	}
            }
        }
        				
//        if (desc == null && id != null) {
//            // Unregistered platform metamodels, e.g. available via "platform:/resource" or "platform:/plugin"
//            URI uri = URI.createURI(id);
//            if (uri.isPlatform()) {
//                desc = MetamodelRegistry.createUndeclaredMetamodel(uri, id, resolutionRS != null ? resolutionRS : new ResourceSetImpl());
//            }
//        }
        
        if (desc == null) {
        	throw new EmfException(NLS.bind(Messages.MetamodelRegistry_0, id, myMetamodelProvider.getMetamodels()));
        }
        
        return desc;      		
	}
}
