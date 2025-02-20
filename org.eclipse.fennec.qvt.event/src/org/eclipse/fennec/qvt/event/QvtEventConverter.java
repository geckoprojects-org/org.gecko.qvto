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
import java.util.Optional;

import org.apache.aries.typedevent.bus.spi.AriesTypedEvents;
import org.apache.aries.typedevent.bus.spi.CustomEventConverter;
import org.apache.aries.typedevent.bus.spi.TypeData;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.qvt.osgi.api.ModelTransformator;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.model.info.EMFModelInfo;
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
	@Reference
	EMFModelInfo infos;

	Map<String, EClassifier> map = new HashMap<>();

	List<ModelTransformator> transformations = new ArrayList<>();
	
	@Reference
	ResourceSet set;
	
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
//		Diagnostic diag = transformator.loadTransformation();
//		CompiledUnit compileUnit = (CompiledUnit) diag.getData().get(0);
//		Module module = compileUnit.getModules().get(0);
//		EList<EOperation> operations = module.getEOperations();
//		for (EOperation operation : operations) {
//			if (operation instanceof EntryOperation eop) {
//				EList<OCLExpression> ocls = eop.getBody().getContent();
//				for (OCLExpression ocl : ocls) {
//					
//					EClassifier source;
//					if(ocl instanceof LoopExp loopExp) {
//						source = loopExp.getIterator().get(0).getType();
//					if(ocl.getEType() instanceof BagType bType) {
//						EClassifier target = (EClassifier) bType.getElementType();
//						
//						
//						System.out.println("++++ "+compileUnit.getName()+" - Source: " + source.getName() + " Target: " + target.getName());
//						Map<String, ModelTransformator> map = ops.computeIfAbsent(source, e -> new HashMap<>());
//						map.put(target.getInstanceClassName(), transformator);
//					}
//					}
//				}
//			}
//			
//			if (operation instanceof MappingOperation mop) {
//				VarParameter context = mop.getContext();
//				System.out.println("+++ operation: " + operation.getName() + " return: " + operation.getEType()
//						+ " context: " + context.getEType());
//				ops.put(context.getEType(), operation);
//			}
//		}
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
//			Map<String, ModelTransformator> map = ops.get(eObject.eClass());
			Optional<EClassifier> oe = infos.getEClassifierForClass(toType.getRawType());
			if (oe.isEmpty()) {
				return null;
			}
			if (oe.get() instanceof EClass eclazz) {
				EClass targetEClass = getTargetEClass(toType, eclazz);
				if (targetEClass == null) {
					return null;
				}
				Optional<ModelTransformator> oMT = transformations.stream()
						.filter(t -> t.canHandle(eObject.eClass(), targetEClass)).findFirst();
				if (oMT.isPresent()) {
					return oMT.get().doTransformation(eObject);
				}
			}
		}
		return null;
	}

	private EClass getTargetEClass(TypeData toType, EClass targetEClass) {
		Object eClassuris = toType.getHandlerProperties().get("aries.event.eClassUris"); // TODO list
		if (eClassuris != null) {
			EClassifier toClass = getEClassifier(eClassuris.toString()); 
			if (toClass == null) {
				System.out.println(""); // TODO throw exception to blacklist eventHandler illegal Type config
				return null;
			}
			
			if(toClass instanceof EClass toEClass) {
				if (!targetEClass.isSuperTypeOf(toEClass)) {
					System.out.println(""); // TODO throw exception to blacklist eventHandler illegal Type config
					return null;
				} 
				targetEClass = toEClass;
			}
		}
		return targetEClass;
	}

	private EClassifier getEClassifier(String eClassuris) {
		return map.computeIfAbsent(eClassuris, uri -> (EClassifier) set.getEObject(URI.createURI(eClassuris), false));
	}

	@Override
	public Map<String, Object> toUntypedEvent(Object eventObject) {
		System.out.println("+++ untyped o " + eventObject);
		return null;
	}

}
