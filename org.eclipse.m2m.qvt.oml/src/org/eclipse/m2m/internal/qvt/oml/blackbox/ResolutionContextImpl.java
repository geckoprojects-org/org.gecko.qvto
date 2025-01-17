/*******************************************************************************
 * Copyright (c) 2008, 2021 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;

public class ResolutionContextImpl implements ResolutionContext {
	
	private final URI fURI;
	private final Set<String> fImports = new HashSet<String>();
	
	public ResolutionContextImpl(URI contextURI) {
		this(contextURI, Collections.<String>emptySet());
	}
	
	public ResolutionContextImpl(URI contextURI, Collection<String> imports) {
		if(contextURI == null) {
			throw new IllegalArgumentException();
		}
		
		fURI = contextURI; 		
		fImports.addAll(imports);
	}
	
	@Override
	public String toString() {	
		return "Resolution context (" + fURI.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public URI getURI() {
		return fURI;
	}
	
	public Collection<String> getImports() {
		return fImports;
	}
}
