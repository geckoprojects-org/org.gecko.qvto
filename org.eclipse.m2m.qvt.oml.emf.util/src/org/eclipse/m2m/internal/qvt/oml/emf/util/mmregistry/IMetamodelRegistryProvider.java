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
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Provides context specific metamodel registry.
 * 
 * @TODO - support context adapter factories to plug-in different context types
 */
public interface IMetamodelRegistryProvider { 
	
	/**
	 * Represents a context in which to look for available metamodels
	 */
	interface IRepositoryContext {
		/**
		 * Gets the URI of this context.
		 * <p>
		 * Note: The URI is required to be <code>file</code> or <code>platform:/resource</code> kind. 
		 * 
		 * @return the URI object, never <code>null</code>
		 */
		URI getURI();
	}

	/**
	 * Gets metamodel registry providing metamodels available in the given context.
	 * <p>
	 * @param context the context in which to lookup available metamodels
	 * 
	 * @return the metamodel registry, never <code>null</code>
	 */
	MetamodelRegistry getRegistry(IRepositoryContext context);
	
	/**
	 * Retrieves a resource set used by this provider to load metamodel resources.
	 * 
	 * @return the resource set, never <code>null</code>
	 */
	ResourceSet getResolutionResourceSet();
}