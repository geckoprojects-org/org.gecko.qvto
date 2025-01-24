/*******************************************************************************
 * Copyright (c) 2009, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 422269, 431082
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.m2m.internal.qvt.oml.library.Context;
import org.eclipse.m2m.qvt.oml.util.ISessionData;
import org.eclipse.m2m.qvt.oml.util.Log;

/**
 * Execution context implementation.
 * 
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @see TransformationExecutor
 */
public final class ExecutionContextImpl implements ExecutionContext {

	private final Map<String, Object> fConfigProperties = new HashMap<String, Object>(5);


	private Log fLog;
	
	private final Map<ISessionData.Entry<Object>, Object> fSessionStorage = new HashMap<ISessionData.Entry<Object>, Object>(5);
	private final ISessionData fSessionData;

	/**
	 * Constructs a default context for execution.
	 */
	public ExecutionContextImpl() {
		fLog = Log.NULL_LOG;
		fSessionData = new Context.SessionDataImpl(fSessionStorage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.m2m.qvt.oml.ExecutionContext#getConfigProperty(java.lang.String)
	 */
	public Object getConfigProperty(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return fConfigProperties.get(name);
	}

	/**
	 * Sets the value object for the given configuration property
	 * 
	 * @param name
	 *            the name of the property, never <code>null</code>
	 * @param value
	 *            the value object
	 */
	public void setConfigProperty(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException("null config property name"); //$NON-NLS-1$
		}

		fConfigProperties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ExecutionContext#getConfigPropertyNames()
	 */
	public Set<String> getConfigPropertyNames() {
		return Collections.unmodifiableSet(fConfigProperties.keySet());
	}

	/**
	 * Sets the log implementation to this context.
	 * 
	 * @param log
	 *            the log implementation, never <code>null</code>
	 */
	public void setLog(Log log) {
		if (log == null) {
			throw new IllegalArgumentException("Non-null logger required"); //$NON-NLS-1$
		}

		this.fLog = log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ExecutionContext#getLog()
	 */
	public Log getLog() {
		return fLog;
	}

	/**
	 * @since 3.4
	 */
	public ISessionData getSessionData() {
		return fSessionData;
	}
	
	/**
	 * @since 3.4
	 */
	public Collection<ISessionData.Entry<Object>> getSessionDataEntries() {
		return fSessionStorage.keySet();
	}
}