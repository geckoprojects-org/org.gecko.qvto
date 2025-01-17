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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.internal.qvt.oml.ast.env.IVirtualOperationTable;
import org.eclipse.m2m.internal.qvt.oml.ast.env.VirtualTable;
import org.eclipse.m2m.internal.qvt.oml.ast.env.VirtualTableAdapter;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstanceFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.ecore.OperationCallExp;

/**
 * This helper class solves the problem of serialization/deserialization of 
 * executable QVTo XMI, with AST extensions like property init expressions.
 * 
 * FIXME - consider elimination of some of the adapters and use only annotations
 * so we can stick to plain serialization. 
 */
public class ExecutableXMIHelper {
	
	private static final String SOURCE_URI_BASE = QvtOperationalParserUtil.QVT_NAMESPACE_URI;
	private static final String VIRTUAL_TABLE_SOURCE = SOURCE_URI_BASE + "/VTable"; //$NON-NLS-1$
	private static final String STATIC_SOURCE = SOURCE_URI_BASE + "/static"; //$NON-NLS-1$
	private static final String OPERATION_CALL_SOURCE = SOURCE_URI_BASE + "/opCall"; //$NON-NLS-1$
	private static final String OPERATION_CALL_DETAILS_OPCODE = "opCode"; //$NON-NLS-1$

	
	public static void fixResourceOnLoad(Resource resource) {
		fixOnLoad(resource.getAllContents());
	}

	public static void fixEObjectOnLoad(EObject eObj) {
		restoreEObjectOnLoad(eObj);
		fixOnLoad(eObj.eAllContents());
	}

	private static void fixOnLoad(TreeIterator<EObject> it) {
		while(it.hasNext()) {
			EObject nextObj = it.next();
			restoreEObjectOnLoad(nextObj);
		}
	}

	private static void restoreEObjectOnLoad(EObject eObj) {
		if(eObj instanceof Module) {
			Module module = (Module) eObj;
			if (false == module.getEFactoryInstance() instanceof ModuleInstanceFactory) {
				module.setEFactoryInstance(new ModuleInstanceFactory());
			}
		} else if(eObj instanceof EOperation) {
			loadVTOperFromAnnotation((EOperation)eObj);
		} else if(eObj instanceof EPackage) {
			EPackage ePackage = (EPackage)eObj;
			if(IntermediateClassFactory.isIntermediatePackage(ePackage)
					&& false == ePackage.getEFactoryInstance() instanceof IntermediateClassFactory) {					
				new IntermediateClassFactory(ePackage);
			}
		} else if(eObj instanceof EStructuralFeature) {
			loadStaticFeatureFromAnnotation((EStructuralFeature) eObj);
		} else if(eObj instanceof OperationCallExp) {
			loadOperationCallDetails((OperationCallExp) eObj);
		}
	}
	
	public static void fixResourceOnSave(Resource rc) {
		fixOnSave(rc.getAllContents());
	}

	public static void fixEObjectOnSave(EObject eObj) {
		prepareEObjectToSave(eObj);
		fixOnSave(eObj.eAllContents());
	}

	private static void fixOnSave(TreeIterator<EObject> it) {
		while(it.hasNext()) {
			EObject nextObj = it.next();
			prepareEObjectToSave(nextObj);
		}
	}
	private static void prepareEObjectToSave(EObject eObj) {
		if(eObj instanceof EOperation) {
			saveVTOperTable2Annotation((EOperation)eObj);
		} else if(eObj instanceof EStructuralFeature) {
			saveStaticFeature((EStructuralFeature) eObj);
		} else if(eObj instanceof OperationCallExp) {
			saveOperationCallDetails((OperationCallExp) eObj);
		}
	}	
	
	private static void loadVTOperFromAnnotation(EOperation operation) {
		EAnnotation annotation = operation.getEAnnotation(VIRTUAL_TABLE_SOURCE);
		if(annotation != null) {
			VirtualTable vt = VirtualTableAdapter.getAdapter(operation, true).getVirtualTable();
			for(EObject next : annotation.getReferences()) {
				if(next instanceof EOperation) {
					vt.addOperation((EOperation)next);
				}
			}
		}
	}
	
	private static void saveVTOperTable2Annotation(EOperation operation) {
		IVirtualOperationTable virtualTable = IVirtualOperationTable.Access.INSTANCE.getVirtualTable(operation);
		if(false  == virtualTable instanceof VirtualTable) {
			return;
		}
		
		if(operation.getEAnnotation(VIRTUAL_TABLE_SOURCE) != null) {
			return;
		}
		
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		operation.getEAnnotations().add(annotation);
		annotation.setSource(VIRTUAL_TABLE_SOURCE);

		VirtualTable vt = (VirtualTable) virtualTable;
		for (EOperation next : vt.getOperations()) {
			annotation.getReferences().add(next);
		}			
	}	

	private static void saveStaticFeature(EStructuralFeature feature) {
		if (feature.getEAnnotation(STATIC_SOURCE) != null) {
			return;
		}
		
		if(IntermediateClassFactory.isFeatureStatic(feature)) {
    		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    		annotation.setSource(STATIC_SOURCE);
    		feature.getEAnnotations().add(annotation);
		}
	}
	
	private static void loadStaticFeatureFromAnnotation(EStructuralFeature feature) {
		EAnnotation annotation = feature.getEAnnotation(STATIC_SOURCE);
		if (annotation != null) {
			IntermediateClassFactory.markFeatureAsStatic(feature);
		}
	}
	
	private static void saveOperationCallDetails(OperationCallExp opCallExp) {
		if (opCallExp.getEAnnotation(OPERATION_CALL_SOURCE) != null) {
			return;
		}

		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(OPERATION_CALL_SOURCE);
		opCallExp.getEAnnotations().add(annotation);

		annotation.getDetails().put(OPERATION_CALL_DETAILS_OPCODE, String.valueOf(opCallExp.getOperationCode()));
	}

	private static void loadOperationCallDetails(OperationCallExp opCallExp) {
		EAnnotation annotation = opCallExp.getEAnnotation(OPERATION_CALL_SOURCE);
		if (annotation == null) {
			return;
		}
		String opCodeString = annotation.getDetails().get(OPERATION_CALL_DETAILS_OPCODE);
		if (opCodeString == null) {
			return;
		}
		try {
			opCallExp.setOperationCode(Integer.valueOf(opCodeString));
		}
		catch (NumberFormatException e) {
		}
	}

}
