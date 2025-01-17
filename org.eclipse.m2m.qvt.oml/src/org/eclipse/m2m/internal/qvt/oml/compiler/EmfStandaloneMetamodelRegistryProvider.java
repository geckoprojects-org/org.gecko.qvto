/*******************************************************************************
 * Copyright (c) 2008, 2018 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.MetamodelRegistry;


/**
 * @author aigdalov
 * Created on Oct 10, 2007
 */
public class EmfStandaloneMetamodelRegistryProvider implements IMetamodelRegistryProvider {
	
	private EPackage.Registry packageRegistry;
	
	private ResourceSet resourceSet;
	
	public EmfStandaloneMetamodelRegistryProvider() {
		this(EPackage.Registry.INSTANCE);
	}
	
	public EmfStandaloneMetamodelRegistryProvider(EPackage.Registry packageRegistry) {
		this.packageRegistry = packageRegistry;
		
		this.resourceSet = new ResourceSetImpl();
		this.resourceSet.setPackageRegistry(new EPackageRegistryImpl(packageRegistry));
	}
	
	public MetamodelRegistry getRegistry(IRepositoryContext context) {
        return new MetamodelRegistry(packageRegistry);
    }
    
    public ResourceSet getResolutionResourceSet() {
    	return resourceSet;
    }
}