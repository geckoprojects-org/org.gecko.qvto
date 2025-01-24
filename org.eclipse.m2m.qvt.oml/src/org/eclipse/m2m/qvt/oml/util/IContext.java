/*******************************************************************************
 * Copyright (c) 2007, 2023 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 431082
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.util;

import java.util.Map;

import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;


/**
 * @noimplement
 * @noextend
 * @since 3.0
 */
public interface IContext {

	/**
	 * Retrieve the logger associated with this context.
	 * <p>
	 * It is used to capture the output of QVT <code>log expression</code>
	 * or various execution diagnostic messages.
	 * 
	 * @return the log object, never <code>null</code>
	 */
	Log getLog();

	/**
	 * Retrieve the Trace object used for incremental update execution associated with this context.
	 * 
	 * @return the trace object, never <code>null</code>
	 * @since 3.5
	 */
    Trace getTrace();
		
    Map<String, Object> getConfigProperties();
    
    Object getConfigProperty(String name);
    
    ISessionData getSessionData();
    
    /**
	 * @since 3.10
	 */
	public ExecutionDiagnostic getExecutionDiagnostic();
}
