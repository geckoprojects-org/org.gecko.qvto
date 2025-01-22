/*******************************************************************************
 * Copyright (c) 2015, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.compiler.ResolverUtils;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolver;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolverFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class PlatformPluginUnitResolverFactory extends UnitResolverFactory {

	@Override
	public boolean accepts(URI uri) {
		return EMFPlugin.IS_ECLIPSE_RUNNING && uri.isPlatformPlugin();
	}

	@Override
	public UnitResolver getResolver(URI uri) {
		if (!uri.isPlatformPlugin() || uri.segmentCount() < 2) {
			return null;
		}

		String bundleId = uri.segment(1);
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			return null;
		}
				
		PlatformPluginUnitResolver resolver;
	
		URI sourceFolderURI = ResolverUtils.getSourceFolderURI(uri);		
		
		if(sourceFolderURI != null && sourceFolderURI.segmentCount() > 2) {
			IPath pluginRelativePath = new Path(sourceFolderURI.toPlatformString(true)).removeFirstSegments(1);
			resolver = new PlatformPluginUnitResolver(bundle, pluginRelativePath);
		} else {
			resolver = new PlatformPluginUnitResolver(bundle);
		}
		
		PlatformPluginUnitResolver.setupResolver(resolver, true, true);
		return resolver;
	}

	@Override
	public String getQualifiedName(URI uri) {
		if (!uri.isPlatformPlugin() || uri.segmentCount() < 2) {
			return null;
		}

		String bundleId = uri.segment(1);
		Bundle bundle = FrameworkUtil.getBundle(getClass());
//		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			return null;
		}
		
		IPath qualifiedName;

		IPath path = new Path(uri.toPlatformString(true));		
		URI sourceFolderURI = ResolverUtils.getSourceFolderURI(uri);		
		
		if(sourceFolderURI != null && sourceFolderURI.segmentCount() > 2) {		
			qualifiedName = new Path(uri.deresolve(sourceFolderURI).trimFileExtension().trimQuery().toString());
		} else {
			qualifiedName = path.removeFirstSegments(1).removeFileExtension();
			
			/**
			 * In case passed URI contains full path then truncate it related to specified source container.
			 */
			for (IPath sourceContainer : PlatformPluginUnitResolver.getSourceContainers(bundle)) {
				int matchCount = qualifiedName.matchingFirstSegments(sourceContainer);
				if (matchCount > 0) {
					qualifiedName = qualifiedName.removeFirstSegments(matchCount);
					break;
				}
			}
		}
		
		return ResolverUtils.toQualifiedName(qualifiedName);
	}

}
