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
 *     Christopher Gerking - bug 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.osgi.util.NLS;

/** @author pkobiakov */
public interface IMetamodelProvider {
		
	class DescImpl implements EPackage.Descriptor {
		
		private final URI uri;
		private final ResourceSet rs;		
		
		protected DescImpl(URI modelResourceUri, ResourceSet resSet) {
			assert modelResourceUri != null;
			assert resSet != null;
			
			if (resSet == null) {
				throw new RuntimeException();
			}
			
			uri = modelResourceUri;
			rs = resSet;
		}
		
		public EFactory getEFactory() {
			return getEPackage().getEFactoryInstance();
		}
	
		public EPackage getEPackage() {
			return loadPackage();
		}
		
		private EPackage loadPackage() {
			
			// bug 376274: support URI fragments that refer to nested packages
			
			EObject eObject = null;
			if (uri.hasFragment()) {
				eObject = rs.getEObject(uri, true);
			}
			if (eObject == null) {
				Resource res = rs.getResource(uri, true);
				eObject = EmfUtil.getFirstEPackageContent(res);
				
				if (eObject != null) {
					String ns = ((EPackage) eObject).getNsURI();
					
					if (ns != null) {
						URI nsUri = URI.createURI(ns);
						rs.getURIConverter().getURIMap().put(nsUri, uri);
					}
				}
			}	
					
			if(eObject instanceof EPackage) {
				return (EPackage) eObject;
			}
						
			throw new WrappedException(new RuntimeException(NLS.bind(Messages.WorskpaceMetamodelProvider_URINotReferringMetamodel, uri)));
		}
	}

	IMetamodelDesc[] getMetamodels();
	
	IMetamodelDesc getMetamodel(String id);
	
	EPackage.Registry getPackageRegistry();
}
