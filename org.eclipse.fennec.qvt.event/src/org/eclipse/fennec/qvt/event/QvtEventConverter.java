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
package org.eclipse.fennec.qvt.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.aries.typedevent.bus.spi.AriesTypedEvents;
import org.apache.aries.typedevent.bus.spi.CustomEventConverter;
import org.apache.aries.typedevent.bus.spi.TypeData;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.fennec.qvt.osgi.api.ModelTransformator;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.ecore.LoopExp;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.ocl.types.BagType;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * 
 */
@RequireEMF
@Component(name = "QvtEventConverter", service = QvtEventConverter.class)
public class QvtEventConverter implements CustomEventConverter {
	@Reference
	private AriesTypedEvents typedEvents;

	Map<EClassifier, Map<String, ModelTransformator>> ops = new HashMap<>();

	List<ModelTransformator> transformations = new ArrayList<>();

	@Activate
	public void activate() {
		System.out.println("+++ activate QvtEventConverter");
		typedEvents.registerGlobalEventConverter(this);
	}

	public void deactivate() {
		System.out.println("+++ deactivate QvtEventConverter");
		typedEvents.registerGlobalEventConverter(null, true);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addTransformation(ModelTransformator transformator, Map<String,Object> properties) {
		Diagnostic diag = transformator.loadTransformation();
		CompiledUnit compileUnit = (CompiledUnit) diag.getData().get(0);
		Module module = compileUnit.getModules().get(0);
		EList<EOperation> operations = module.getEOperations();
		for (EOperation operation : operations) {
			if (operation instanceof EntryOperation eop) {
				EList<OCLExpression> ocls = eop.getBody().getContent();
				for (OCLExpression ocl : ocls) {
					
					EClassifier source;
					if(ocl instanceof LoopExp loopExp) {
						source = loopExp.getIterator().get(0).getType();
					if(ocl.getEType() instanceof BagType bType) {
						EClass target = (EClass) bType.getElementType();
						System.out.println("++++ "+compileUnit.getName()+" - Source: " + source.getName() + " Target: " + target.getName());
						Map<String, ModelTransformator> map = ops.computeIfAbsent(source, e -> new HashMap<>());
						map.put(target.getInstanceClassName(), transformator);
					}
					}
				}
			}
			
//			if (operation instanceof MappingOperation mop) {
//				VarParameter context = mop.getContext();
//				System.out.println("+++ operation: " + operation.getName() + " return: " + operation.getEType()
//						+ " context: " + context.getEType());
//				ops.put(context.getEType(), operation);
//			}
		}
		System.out.println("+++ add:" + transformator);
		transformations.add(transformator);
	}

	public void removeTransformation(ModelTransformator transformator) {
		System.out.println("+++ remove:" + transformator);
		transformations.remove(transformator);
	}

	@Override
	public <T> T toTypedEvent(Object eventObject, TypeData toType) {
		System.out.println("+++ typed o " + eventObject + "\ttype" + toType);
		if (eventObject instanceof EObject eObject) {
			Map<String, ModelTransformator> map = ops.get(eObject.eClass());
			ModelTransformator transformator = map.get(toType.getRawTypeString());
			if(transformator != null) {
				return transformator.doTransformation(eObject);
			}
//			EOperation operation = ops.get(eObject.eClass());
//			if (operation != null)
//				try {
//					eObject.eInvoke(operation, ECollections.emptyEList());
//
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			for (ModelTransformator modelTransformator : transformations) {
//				EObject resultEObject = modelTransformator.doTransformation(eObject);
//				if (toType.getRawType().isAssignableFrom(resultEObject.getClass())) {
//					return (T) resultEObject;
//				}
//			}
		}
		return null;
	}

	@Override
	public Map<String, Object> toUntypedEvent(Object eventObject) {
		System.out.println("+++ untyped o " + eventObject);
		return null;
	}

}
