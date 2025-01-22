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

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;

/**
 * The main plugin class to be used in the desktop.
 */
//public class EmfUtilPlugin extends Plugin {
public class EmfUtilPlugin  {

	//The shared instance.
	private static EmfUtilPlugin plugin = new EmfUtilPlugin();
	
	/**
	 * The constructor.
	 */
	private EmfUtilPlugin() {
	}

	/**
	 * Returns the shared instance.
	 */
	public static EmfUtilPlugin getDefault() {
		return plugin;
	}
	
	public static BasicDiagnostic createDiagnostic(String message) {
		return new BasicDiagnostic(Diagnostic.OK, ID, 0, message, null);
	}

	public static Diagnostic createErrorDiagnostic(String message, Throwable throwable) {
		Object[] data = (throwable == null) ? null : new Object [] { throwable };
		return new BasicDiagnostic(Diagnostic.ERROR, ID, 0, message, data);
	}
	
	public static Diagnostic createWarnDiagnostic(String message) {
		return new BasicDiagnostic(Diagnostic.ERROR, ID, 0, message, null);
	}	

	/**
	 * Indicates that the given diagnostic is neither error or canceled.
	 * 
	 * @param diagnostic
	 *            the diagnostic to test
	 * @return <code>true</code> in case of success, <code>false</code>
	 *         otherwise
	 */
	public static boolean isSuccess(Diagnostic diagnostic) {
		int severity = diagnostic.getSeverity();
		return severity != Diagnostic.ERROR && severity != Diagnostic.CANCEL;
	}
		
	public static final String ID = "org.eclipse.m2m.qvt.oml.emf.util"; //$NON-NLS-1$
}
