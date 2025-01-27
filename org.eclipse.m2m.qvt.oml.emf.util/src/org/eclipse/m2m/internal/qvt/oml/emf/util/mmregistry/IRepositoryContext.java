/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import org.eclipse.emf.common.util.URI;

/**
 * Represents a context in which to look for available metamodels
 */
public interface IRepositoryContext {
	/**
	 * Gets the URI of this context.
	 * <p>
	 * Note: The URI is required to be <code>file</code> or <code>platform:/resource</code> kind. 
	 * 
	 * @return the URI object, never <code>null</code>
	 */
	URI getURI();
}