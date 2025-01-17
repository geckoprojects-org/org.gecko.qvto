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
package org.eclipse.m2m.internal.qvt.oml.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;

/**
 * @author pkobiakov
 */
public class MdaException extends Exception {
	private static final long serialVersionUID = 4003559255399844949L;
	
	public MdaException(IStatus status) {
		this(status.getMessage(), status);
	}
	
	public MdaException(String message) {
		this(new Status(IStatus.ERROR, CommonPlugin.ID, message));
	}
	
	public MdaException(String message, Throwable cause) {
		this(message, BasicDiagnostic.toIStatus(BasicDiagnostic.toDiagnostic(cause)));
	}
	
	public MdaException(Throwable cause) {
		this(cause.getMessage(), cause);
	}
	
	private MdaException(String message, IStatus status) {
		super(message);
		myStatus = status;
	}
	
	public IStatus getStatus() {
		return myStatus;
	}
	
	@Override
	public synchronized Throwable getCause() {
		return myStatus.getException();
	}
	
	@Override
	public String getMessage() {
		String msg = super.getMessage();
		String statusMsg = getStatusMessage(myStatus);
		if (statusMsg.startsWith(msg)) {
			return statusMsg;
		}
		return msg + " [" + statusMsg + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private static String getStatusMessage(IStatus status) {
		String children = "";
		for (IStatus childStatus : status.getChildren()) {
			if (children.length() > 0)  {
				children += ',';
			}
			children += getStatusMessage(childStatus);
		}

		String msg = status.getMessage();
		if (children.length() > 0)  {
			msg += " [" + children + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return msg;
	}
	
	private IStatus myStatus; 
}
