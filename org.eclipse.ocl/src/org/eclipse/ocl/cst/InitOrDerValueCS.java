/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation, Zeligsoft Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   Zeligsoft - Bug 243976
 *******************************************************************************/
package org.eclipse.ocl.cst;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Init Or Der Value CS</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.ocl.cst.InitOrDerValueCS#getExpressionCS <em>Expression CS</em>}</li>
 * </ul>
 *
 * @see org.eclipse.ocl.cst.CSTPackage#getInitOrDerValueCS()
 * @model abstract="true"
 * @generated
 */
public interface InitOrDerValueCS
		extends CSTNode {

	/**
	 * Returns the value of the '<em><b>Expression CS</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression CS</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression CS</em>' containment reference.
	 * @see #setExpressionCS(OCLExpressionCS)
	 * @see org.eclipse.ocl.cst.CSTPackage#getInitOrDerValueCS_ExpressionCS()
	 * @model containment="true"
	 *        extendedMetaData="name='oclExpressionCS'"
	 * @generated
	 */
	OCLExpressionCS getExpressionCS();

	/**
	 * Sets the value of the '{@link org.eclipse.ocl.cst.InitOrDerValueCS#getExpressionCS <em>Expression CS</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression CS</em>' containment reference.
	 * @see #getExpressionCS()
	 * @generated
	 */
	void setExpressionCS(OCLExpressionCS value);

} // InitOrDerValueCS
