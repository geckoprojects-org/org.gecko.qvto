/*******************************************************************************
 * Copyright (c) 2007, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 376274, 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.eclipse.EmfUtilPlugin;
import org.eclipse.m2m.internal.qvt.oml.emf.util.urimap.MappingContainer;
import org.eclipse.m2m.internal.qvt.oml.emf.util.urimap.MetamodelURIMappingHelper;
import org.eclipse.m2m.internal.qvt.oml.emf.util.urimap.URIMapping;
import org.eclipse.osgi.util.NLS;

public class ProjectMetamodelProvider extends DelegatingMetamodelProvider {

	private ResourceSet resSet;
	private List<IMetamodelDesc> metamodels;
	
	public ProjectMetamodelProvider(IProject project) {
		this(project, BaseMetamodelRegistry.getDefaultMetamodelProvider(), new ResourceSetImpl());
	}
				
	public ProjectMetamodelProvider(IProject project, IMetamodelProvider delegate, ResourceSet resolutionRSet) {
		super(delegate);
		
		if(resolutionRSet == null) {
			throw new IllegalArgumentException("Null resolution resource set"); //$NON-NLS-1$
		}
		
		this.resSet = resolutionRSet;
		this.metamodels = new ArrayList<IMetamodelDesc>();
		
		try {
			MappingContainer mappings = MetamodelURIMappingHelper.loadMappings(project);
		
			for (URIMapping nextMapping : mappings.getMapping()) {
				URI uri = null;
				IllegalArgumentException error = null;
				try {
					uri = URI.createURI(nextMapping.getTargetURI());
				} catch (IllegalArgumentException e) {
					error = e;
				}
	
				if (uri != null && nextMapping.getSourceURI() != null) {					
					EPackage.Descriptor ePackageDesc = new DescImpl(uri, resSet);
					IMetamodelDesc desc = new EmfMetamodelDesc(ePackageDesc, nextMapping.getSourceURI());
					metamodels.add(desc);
				} else {
	        		String message = NLS.bind("Invalid metamodel uri mapping. nsUri:''{0}'' modelUri:''{1}''", //$NON-NLS-1$
	        				nextMapping.getSourceURI(), nextMapping.getTargetURI());
					EmfUtilPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, EmfUtil.ID, message, error));
	
				}
			} 
		} catch (IOException e) {
			EmfUtilPlugin.log(e);
		}	
	}
	
	protected IMetamodelDesc[] getLocalMetamodels() {
		return metamodels.toArray(new IMetamodelDesc[metamodels.size()]);
	}
	
}