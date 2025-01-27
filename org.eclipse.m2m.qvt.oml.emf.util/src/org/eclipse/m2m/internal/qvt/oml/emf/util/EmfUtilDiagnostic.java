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
package org.eclipse.m2m.internal.qvt.oml.emf.util;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;

/**
 * 
 * @author grune
 * @since Jan 27, 2025
 */
public class EmfUtilDiagnostic {
	public static BasicDiagnostic createDiagnostic(String message) {
		return new BasicDiagnostic(Diagnostic.OK, EmfUtil.ID, 0, message, null);
	}
	
	public static Diagnostic createErrorDiagnostic(String message, Throwable throwable) {
		Object[] data = (throwable == null) ? null : new Object [] { throwable };
		return new BasicDiagnostic(Diagnostic.ERROR, EmfUtil.ID, 0, message, data);
	}
	
	public static Diagnostic createWarnDiagnostic(String message) {
		return new BasicDiagnostic(Diagnostic.ERROR, EmfUtil.ID, 0, message, null);
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
}

