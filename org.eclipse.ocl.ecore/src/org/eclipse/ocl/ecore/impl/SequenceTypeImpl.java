/*******************************************************************************
 * Copyright (c) 2006, 2018 IBM Corporation, Zeligsoft Inc., and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Zeligsoft - Bug 241426
 *******************************************************************************/
package org.eclipse.ocl.ecore.impl;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.ocl.ecore.EcorePackage;
import org.eclipse.ocl.ecore.SequenceType;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.operations.SequenceTypeOperations;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sequence Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class SequenceTypeImpl
		extends CollectionTypeImpl
		implements SequenceType {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected SequenceTypeImpl() {
		super();
		setInstanceClass(List.class);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected SequenceTypeImpl(EClassifier elementType) {
		super(elementType);
		setInstanceClass(List.class);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EcorePackage.Literals.SEQUENCE_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public CollectionKind getKind() {
		return CollectionKind.SEQUENCE_LITERAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public boolean checkCollectionTypeName(DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return SequenceTypeOperations.checkCollectionTypeName(this, diagnostics,
			context);
	}

} //SequenceTypeImpl
