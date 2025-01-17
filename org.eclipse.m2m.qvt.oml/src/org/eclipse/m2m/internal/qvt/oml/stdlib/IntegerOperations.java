/*******************************************************************************
 * Copyright (c) 2008, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.ocl.types.OCLStandardLibrary;



public class IntegerOperations extends AbstractContextualOperations {

	public IntegerOperations(AbstractQVTStdlib library) {
		super(library, library.getEnvironment().getOCLStandardLibrary().getInteger());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		EClassifier listOfInteger = getStdlib().getEnvironment().getTypeResolver().resolveListType(oclStdlib.getInteger());

		return new OperationProvider[] {
			new OperationProvider(UNSUPPORTED_OPER, "range", new String[] {"start", "end"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					listOfInteger, oclStdlib.getInteger(), oclStdlib.getInteger()),
		};
	}
	
}
