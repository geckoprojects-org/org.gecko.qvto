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
 *     Christopher Gerking - bugs 431082, 537041
 *******************************************************************************/

package org.eclipse.m2m.internal.qvt.oml.runtime.project;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.compiler.QVTOCompiler;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitProxy;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolverFactory;
import org.eclipse.m2m.internal.qvt.oml.emf.util.eclipse.URIUtils;
import org.eclipse.m2m.internal.qvt.oml.emf.util.eclipse.WorkspaceUtils;

public class QvtCompilerFacade {
	
	public static interface CompilationResult {
		
		QVTOCompiler getCompiler();
		
		CompiledUnit getCompiledModule();
	}
	
	private QvtCompilerFacade() {
	}

	public static CompilationResult getCompiledModule(URI uriTransf, QvtCompilerOptions compilerOptions) throws MdaException {
		// FIXME - why is that relied on being it an IFile?
		IFile ifile = WorkspaceUtils.getWorkspaceFile(uriTransf);
		return getCompiledModule(ifile, compilerOptions);
	}
	
	static CompilationResult getCompiledModule(IFile ifile, QvtCompilerOptions compilerOptions) throws MdaException {
        	URI resourceURI = URIUtils.getResourceURI(ifile);
			UnitProxy sourceUnit = UnitResolverFactory.Registry.INSTANCE.getUnit(resourceURI);
						
			if(sourceUnit == null) {
				throw new MdaException("Failed to resolve compilation unit: " + ifile); //$NON-NLS-1$
			}			

			final QVTOCompiler compiler = new QVTOCompiler();
			final CompiledUnit module = compiler.compile(sourceUnit, compilerOptions);
			
			return new CompilationResult() {
				public CompiledUnit getCompiledModule() {
					return module;
				}
				public QVTOCompiler getCompiler() {
					return compiler;
				}				
			};
	}
}
