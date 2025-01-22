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
 *     Christopher Gerking - bugs 376274, 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class WorkspaceMetamodelProvider implements IMetamodelProvider {

	private ResourceSet resSet;
	
//	private IWorkspace workspace;
	
	public WorkspaceMetamodelProvider() {
		this(new ResourceSetImpl());
	}
	
	public WorkspaceMetamodelProvider(ResourceSet resolutionRSet) {
//		this(ResourcesPlugin.getWorkspace(), resolutionRSet);
		this.resSet = resolutionRSet;
	}
			
//	private WorkspaceMetamodelProvider(IWorkspace workspace, ResourceSet resolutionRSet) {		
//		this.workspace = workspace;
//		this.resSet = resolutionRSet;
//	}
			
	private IMetamodelDesc createMetamodelDesc(String metamodelID, URI metamodelResourceURI) {
		if(metamodelID == null) {
			throw new IllegalArgumentException("Null metamodel ID"); //$NON-NLS-1$			
		}
				
		if(metamodelResourceURI == null) {
			throw new IllegalArgumentException("Null metamodel resource URI"); //$NON-NLS-1$
		}
				
		EPackage.Descriptor ePackageDesc = new DescImpl(metamodelResourceURI, resSet);
		IMetamodelDesc desc = new EmfMetamodelDesc(ePackageDesc, metamodelID);
		return desc;
	}
	
	public IMetamodelDesc getMetamodel(String id) {
			
		IPath path = Path.fromPortableString(id);
//		IResource resource = workspace.getRoot().findMember(path);
		
//		if (resource != null) {
//			URI resourceUri = URIUtils.getResourceURI(resource);
//			return createMetamodelDesc(path.makeRelative().toPortableString(), resourceUri);
//		}
//		else {
			return null;
//		}
	}
	
	public EPackage.Registry getPackageRegistry() {
		EPackage.Registry registry = new EPackageRegistryImpl();
		
		for (IMetamodelDesc desc : getMetamodels()) {
			EPackage ePackage = desc.getModel();
			registry.put(ePackage.getNsURI(), ePackage);
		}
		
		return registry;
	}
	
	public IMetamodelDesc[] getMetamodels() {
		final List<IMetamodelDesc> metamodels = new ArrayList<IMetamodelDesc>();
			
//		try {
//			workspace.getRoot().accept(new IResourceProxyVisitor() {
//				public boolean visit(IResourceProxy proxy) throws CoreException {
//					if(proxy.getType() == IResource.FILE && MetamodelRegistry.isMetamodelFileName(proxy.getName())) {
//						URI resourceUri = URIUtils.getResourceURI(proxy.requestResource());
//						IMetamodelDesc desc = createMetamodelDesc(proxy.requestFullPath().makeRelative().toPortableString(), resourceUri);
//						metamodels.add(desc);
//					}
//					return true;
//				}
//			}, IResource.NONE);
//		} catch (CoreException e) {
//			EmfUtilPlugin.log(e);
//		}
		
		return metamodels.toArray(new IMetamodelDesc[0]);	
	}
		
//	/**
//	 * Loads a metamodel from resource denoted by the given URI.
//	 * 
//	 * @throws RuntimeException if an EPackage instance can't be obtained from the given resource
//	 * @throws IllegalArgumentException if <code>uriStr</code> is not a valid URI
//	 */
//	public static EPackage loadResourceMetamodel(String uriStr) {
//		return loadResourceMetamodel(uriStr, new ResourceSetImpl());
//	}
//		
//	public static EPackage loadResourceMetamodel(String uriStr, ResourceSet rs) {
//		IMetamodelDesc desc = new WorkspaceMetamodelProvider(rs).getMetamodel(URI.createURI(uriStr).toPlatformString(true));
//				
//		return desc.getModel();
//	}
	
}
