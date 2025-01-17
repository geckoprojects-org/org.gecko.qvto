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
package org.eclipse.m2m.internal.qvt.oml;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;

/**
 * @author feldman
 */
public class QvtMessage extends BasicDiagnostic {
	
	public static final int SEVERITY_ERROR = Diagnostic.ERROR;
	
	public static final int SEVERITY_WARNING = Diagnostic.WARNING;
	
	public QvtMessage(final String message, final int severity, final int offset, final int length, int lineNum) {
		super(severity, QvtPlugin.ID, 0, message, new Object[] {});

		myPos = new TextPositionsImpl(offset, length);
		this.myLineNum = lineNum;
	}

	public QvtMessage(final String message, final int offset, final int length) {
		this(message, SEVERITY_ERROR, offset, length, -1);
	}
	
	public QvtMessage(final String message) {
		this(message, 0, 0);
	}
	
	public int getOffset() {
		return myPos.getOffset();
	}

	public int getLength() {
		return myPos.getLength();
	}
	
	public int getLineNum() {
		return myLineNum;
	}
	
	@Override
	public String toString() {
		return (getLineNum() > 0 ? "Line " + getLineNum() + ':' : myPos.toString()) + " " + getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof QvtMessage == false) {
			return false;
		}
		
		QvtMessage other = (QvtMessage)o;
		
		if(getSeverity() != other.getSeverity()) {
			return false;
		}
		
		if (getMessage() == null ? other.getMessage() != null : !getMessage().equals(other.getMessage())) {
			return false;
		}
		
		if (myPos == null ? other.myPos != null : !myPos.equals(other.myPos)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash*37 + getSeverity();
		
		if (getMessage() != null) {
			hash = hash*37 + getMessage().hashCode();
		}
		
		if (myPos != null) {
			hash = hash*37 + myPos.hashCode();
		}
		
		return hash;
	}

	private class TextPositionsImpl {

		public TextPositionsImpl(final int offset, final int length) {
			myOffset = offset;
			myLength = length;
		}
		
		public int getOffset() {
			return myOffset;
		}
		
		public int getLength() {
			return myLength;
		}
		
		@Override
		public String toString() {
			return "[" + myOffset + ":" + (myOffset + myLength) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof TextPositionsImpl == false) {
				return false;
			}
			
			TextPositionsImpl other = (TextPositionsImpl) o;
			return myOffset == other.myOffset && myLength == other.myLength;
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash*37 + myOffset;
			hash = hash*37 + myLength;
			
			return hash;
		}
		
		private final int myOffset;
		private final int myLength;
	}
	
	private final int myLineNum;
	private final TextPositionsImpl myPos;
}