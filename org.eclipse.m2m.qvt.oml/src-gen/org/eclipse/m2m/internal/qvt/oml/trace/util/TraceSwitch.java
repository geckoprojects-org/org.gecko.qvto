/**
 * Copyright (c) 2007, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *
 * $Id$
 */
package org.eclipse.m2m.internal.qvt.oml.trace.util;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults;
import org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue;
import org.eclipse.m2m.internal.qvt.oml.trace.EValue;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;
import org.eclipse.m2m.internal.qvt.oml.trace.TracePackage;
import org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord;
import org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage
 * @generated
 */
public class TraceSwitch<T> extends Switch<T> {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public static final String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v2.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v20.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The cached model package
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected static TracePackage modelPackage;

    /**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public TraceSwitch() {
		if (modelPackage == null) {
			modelPackage = TracePackage.eINSTANCE;
		}
	}

    /**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
    @Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case TracePackage.TRACE: {
				Trace trace = (Trace)theEObject;
				T result = caseTrace(trace);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.TRACE_RECORD: {
				TraceRecord traceRecord = (TraceRecord)theEObject;
				T result = caseTraceRecord(traceRecord);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.VAR_PARAMETER_VALUE: {
				VarParameterValue varParameterValue = (VarParameterValue)theEObject;
				T result = caseVarParameterValue(varParameterValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<MappingOperation, EList<TraceRecord>> mappingOperationToTraceRecordMapEntry = (Map.Entry<MappingOperation, EList<TraceRecord>>)theEObject;
				T result = caseMappingOperationToTraceRecordMapEntry(mappingOperationToTraceRecordMapEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.EMAPPING_OPERATION: {
				EMappingOperation eMappingOperation = (EMappingOperation)theEObject;
				T result = caseEMappingOperation(eMappingOperation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.EVALUE: {
				EValue eValue = (EValue)theEObject;
				T result = caseEValue(eValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.ETUPLE_PART_VALUE: {
				ETuplePartValue eTuplePartValue = (ETuplePartValue)theEObject;
				T result = caseETuplePartValue(eTuplePartValue);
				if (result == null) result = caseEValue(eTuplePartValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.EMAPPING_CONTEXT: {
				EMappingContext eMappingContext = (EMappingContext)theEObject;
				T result = caseEMappingContext(eMappingContext);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.EMAPPING_PARAMETERS: {
				EMappingParameters eMappingParameters = (EMappingParameters)theEObject;
				T result = caseEMappingParameters(eMappingParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.EMAPPING_RESULTS: {
				EMappingResults eMappingResults = (EMappingResults)theEObject;
				T result = caseEMappingResults(eMappingResults);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TracePackage.OBJECT_TO_TRACE_RECORD_MAP_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<Object, EList<TraceRecord>> objectToTraceRecordMapEntry = (Map.Entry<Object, EList<TraceRecord>>)theEObject;
				T result = caseObjectToTraceRecordMapEntry(objectToTraceRecordMapEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Trace</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Trace</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseTrace(Trace object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Record</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Record</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseTraceRecord(TraceRecord object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Var Parameter Value</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Var Parameter Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseVarParameterValue(VarParameterValue object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Mapping Operation To Trace Record Map Entry</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapping Operation To Trace Record Map Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseMappingOperationToTraceRecordMapEntry(Map.Entry<MappingOperation, EList<TraceRecord>> object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EMapping Operation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EMapping Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseEMappingOperation(EMappingOperation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EValue</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EValue</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseEValue(EValue object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>ETuple Part Value</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ETuple Part Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseETuplePartValue(ETuplePartValue object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EMapping Context</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EMapping Context</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseEMappingContext(EMappingContext object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EMapping Parameters</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EMapping Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseEMappingParameters(EMappingParameters object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EMapping Results</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EMapping Results</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseEMappingResults(EMappingResults object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Object To Trace Record Map Entry</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Object To Trace Record Map Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseObjectToTraceRecordMapEntry(Map.Entry<Object, EList<TraceRecord>> object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
    @Override
	public T defaultCase(EObject object) {
		return null;
	}

} //TraceSwitch
