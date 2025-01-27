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
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.BaseMetamodelRegistry;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistry;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IRepositoryContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


/**
 * @author aigdalov
 * Created on Oct 10, 2007
 */
@Component(service = IMetamodelRegistryProvider.class)
public class EmfStandaloneMetamodelRegistryProvider implements IMetamodelRegistryProvider {
	
	private EPackage.Registry packageRegistry;
	
	private ResourceSet resourceSet;
	
	@Activate
	public void activate() {
		
	}
	
	public EmfStandaloneMetamodelRegistryProvider() {
		this(EPackage.Registry.INSTANCE);
	}
	
	public EmfStandaloneMetamodelRegistryProvider(EPackage.Registry packageRegistry) {
		this.packageRegistry = packageRegistry;
		
		this.resourceSet = new ResourceSetImpl();
		this.resourceSet.setPackageRegistry(new EPackageRegistryImpl(packageRegistry));
	}
	
	public IMetamodelRegistry getRegistry(IRepositoryContext context) {
        return new BaseMetamodelRegistry(packageRegistry);
    }
    
    public ResourceSet getResolutionResourceSet() {
    	return resourceSet;
    }
}