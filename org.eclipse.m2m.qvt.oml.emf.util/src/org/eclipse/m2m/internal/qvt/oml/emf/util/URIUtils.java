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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

public class URIUtils {
	private URIUtils() {
	}

	public static URI getResourceURI(IResource resource) {
		return URI.createPlatformResourceURI(resource.getFullPath().toString(), true);
	}
	
	private static IResource getResource(URI resourceURI) {
//		IWorkspaceRoot wsRoot;
//		try {
//			wsRoot = ResourcesPlugin.getWorkspace().getRoot();
//		}
//		catch (RuntimeException e) {
//			return null;
//		}
//		
//		if(resourceURI.isPlatformResource()) {
//			String wsRelativePath = resourceURI.toPlatformString(true);
//			return wsRoot.findMember(new Path(wsRelativePath));			
//		} else if(resourceURI.isFile() && !resourceURI.isRelative()) {
//            IFile[] files;
//			try {
//				files = wsRoot.findFilesForLocationURI(new java.net.URI(resourceURI.toString()));
//	            if (files.length > 0) {
//	            	return files[0];
//	            }
//				
//			} catch (URISyntaxException e) {
//				// do nothing just indicate we could not resolve to a resource
//				return null;
//			}
//		}
//
		return null;
	}
	
	public static IFile getFile(URI uri) {
		IResource resource = getResource(uri);
		return resource instanceof IFile file ? file : null;
	}

	public static void refresh(URI uri) {
	    IFile file = URIUtils.getFile(uri);
	    if(file != null) {
	        try {
	            file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
	        } 
	        catch (CoreException e) {
	        }
	    }
	}
		
}
