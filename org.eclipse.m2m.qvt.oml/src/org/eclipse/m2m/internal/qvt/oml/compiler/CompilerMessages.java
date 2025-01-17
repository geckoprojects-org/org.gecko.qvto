/*******************************************************************************
 * Copyright (c) 2007, 2019 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import org.eclipse.m2m.internal.qvt.oml.NLS;

public class CompilerMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.m2m.internal.qvt.oml.compiler.CompilerMessages"; //$NON-NLS-1$

	public static String sourceReadingIOError;
	public static String importedCompilationUnitNotFound;
	public static String compilationUnitAlreadyImported;	
	public static String cyclicImportError;
	public static String moduleNotFound;	
	
	public static String importHasCompilationError;
	public static String emptyImport;	

	public static String moduleTransformationExpected;
	
	public static String unitDiagnostic;
	
	public static String QvtCompilerFacade_compilingScript;
	public static String QvtCompilerFacade_acquiringScript;
	
	public static String parsingTaskName;
	public static String analyzingTaskName;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CompilerMessages.class);
	}

	private CompilerMessages() {
	}
}
