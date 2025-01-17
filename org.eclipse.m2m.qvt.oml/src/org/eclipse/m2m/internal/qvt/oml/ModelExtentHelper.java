/*******************************************************************************
 * Copyright (c) 2009, 2018 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *     C.Gerking/E.Willink - Bug 534077
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.AbstractEList;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfException;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.URIUtils;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTEvaluationOptions;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ModelExtent;


public class ModelExtentHelper {

	private static class ExtentEntry {

		final ModelExtent extent;
		final URI saveAsURI;

		ExtentEntry(ModelExtent extent, URI saveAsURI) {
			assert extent != null;
			this.extent = extent;
			this.saveAsURI = saveAsURI;
		}
	}


	private final OperationalTransformation fTransformation;
	private final List<URI> fModelExtentURIs;
	private final LinkedHashMap<ModelParameter, ExtentEntry> fExtentMap;
	private final ResourceSet fResourceSet;


	public ModelExtentHelper(OperationalTransformation transformation, List<URI> modelExtentURIs, ResourceSet resSet) {
		if(transformation == null || modelExtentURIs == null || modelExtentURIs.contains(null)) {
			throw new IllegalArgumentException();
		}

		fTransformation = transformation;
		fModelExtentURIs = modelExtentURIs;
		fExtentMap = new LinkedHashMap<ModelParameter, ExtentEntry>();
		fResourceSet = resSet;
	}

	public ResourceSet getResourceSet() {
		return fResourceSet;
	};

	public Diagnostic saveExtents(Boolean isQvtoUnparseEnabled) {
		BasicDiagnostic diagnostic = QvtPlugin.createDiagnostic("Save model param extents diagnostic");

		Set<Resource> resources = new HashSet<Resource>();

		for (ExtentEntry nextEntry : fExtentMap.values()) {
			URI saveAsURI = nextEntry.saveAsURI;
			if(saveAsURI != null) {
				resources.addAll(addExtentContentsToResources(nextEntry.extent.getContents(), fResourceSet, saveAsURI));
			}
		}

		Map<Object, Object> options = new LinkedHashMap<Object, Object>(EmfUtil.DEFAULT_SAVE_OPTIONS);
		options.put(QVTEvaluationOptions.FLAG_QVTO_UNPARSE_ENABLED, isQvtoUnparseEnabled);

		for (Resource outExtent : resources) {
			try {
				EmfUtil.saveModel(outExtent, options);
			}
			catch (EmfException e) {
				diagnostic.add(BasicDiagnostic.toDiagnostic(e));
			}

			URIUtils.refresh(outExtent.getURI());
		}

		return diagnostic;
	}

	public List<ModelExtent> loadExtents() throws DiagnosticException {
		List<ModelParameter> params = fTransformation.getModelParameter();
		if(params.size() > fModelExtentURIs.size()) {
			throw new DiagnosticException(QvtPlugin.createErrorDiagnostic(NLS.bind(
					Messages.InvalidModelParameterCountError, fModelExtentURIs
					.size(), params.size()), null));
		}

		int i = 0;
		for (ModelParameter nextParam : params) {
			URI uri = fModelExtentURIs.get(i++);
			DirectionKind kind = nextParam.getKind();
			URI saveAsURI = (kind != DirectionKind.IN) ? uri : null;
			URI loadURI = (kind != DirectionKind.OUT) ? uri : null;

			ModelExtent extent;
			if(loadURI != null) {
				extent = loadExtent(nextParam, loadURI, fResourceSet);
			} else {
				extent = new BasicModelExtent();
			}

			ExtentEntry entry = new ExtentEntry(extent, saveAsURI);
			fExtentMap.put(nextParam, entry);
		}

		return getExtents();
	}

	private ModelExtent loadExtent(ModelParameter modelParameter, URI uri, ResourceSet rs) throws DiagnosticException {
		Throwable error;
		try {
			Resource res = rs.getResource(uri, true);
			EList<EObject> contents = res.getContents();

			if(!res.getErrors().isEmpty()) {
				for (Resource.Diagnostic d : res.getErrors()) {
					// just take the first to manifest something is wrong
					throw new DiagnosticException(QvtPlugin.createErrorDiagnostic(
							d.getMessage(), null));
				}
			}

			return new BasicModelExtent(contents);

		} catch (WrappedException e) {
			error = (e.getCause() != null) ? e.getCause() : e;
		} catch (RuntimeException e) {
			error = e;
		}

		throw new DiagnosticException(QvtPlugin.createErrorDiagnostic("Failed to load model extent uri=" + uri, error));
	}

	private List<ModelExtent> getExtents() {
		List<ModelExtent> result = new ArrayList<ModelExtent>(fExtentMap.size());
		for (ExtentEntry extentEntry : fExtentMap.values()) {
			result.add(extentEntry.extent);
		}
		return result;
	}

	/**
	 * Adds given MOF extent (represented by 'rootContents') to all appropriate resources in given ResourceSet (represented by 'resSet').
	 * Takes into account that EObject's containment hierarchy may split between different Resources.
	 *
	 * @param rootContents
	 * @param resSet
	 * @param outUri
	 * @return the set of affected resources
	 */
	private static Set<Resource> addExtentContentsToResources(List<EObject> rootContents, ResourceSet resSet, URI outUri) {
		URI modelUri = outUri.trimFragment();
		Resource outExtent = resSet.getResource(modelUri, false);
		if(outExtent == null) {
			outExtent = EmfUtil.createResource(modelUri, resSet);
			resSet.getResources().add(outExtent);
		}

		Map<Resource, List<EObject>> linkedExtents = new LinkedHashMap<Resource, List<EObject>>();
		linkedExtents.put(outExtent, rootContents);

		TreeIterator<EObject> allContents = EcoreUtil.getAllContents(rootContents, false);
		while (allContents.hasNext()) {
			EObject eObject = allContents.next();
			if (!rootContents.contains(eObject)) {
				Resource eResource = eObject.eResource();
				if (eResource != null && eResource != outExtent) {
					List<EObject> content = linkedExtents.get(eResource);
					if (content == null) {
						content = new ArrayList<EObject>();
						linkedExtents.put(eResource, content);
					}
					content.add(eObject);
				}
			}
		}

		for (Map.Entry<Resource, List<EObject>> nextExtent : linkedExtents.entrySet()) {
			Resource rs = nextExtent.getKey();
			mergeExtentToResource(rs, nextExtent.getValue());
		}

		return linkedExtents.keySet();
	}

	public static void mergeExtentToResource(Resource outExtent, List<? extends EObject> extentContent) {
		Set<EObject> essentialRootElements = getEssentialRootElements(extentContent);
		if (essentialRootElements.isEmpty()) {
			for (TreeIterator<EObject> it = outExtent.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				eObject.eAdapters().clear();
			}
			outExtent.getContents().clear();
		}
		else if (outExtent.getContents().isEmpty()) {
			addAllContents(outExtent.getContents(), essentialRootElements);
		}
		else {
			Set<EObject> resolvedRootElements = EmfUtil.getResolvedContent(essentialRootElements, outExtent.getContents().get(0));

			for (TreeIterator<EObject> it = outExtent.getAllContents(); it.hasNext();) {
				EObject eObject = it.next();
				if (!resolvedRootElements.contains(eObject)) {
					eObject.eAdapters().clear();
				}
			}

			outExtent.getContents().retainAll(resolvedRootElements);

			for (EObject eObject : outExtent.getContents()) {
				InternalEObject internalEObject = (InternalEObject) eObject;
				if (internalEObject.eDirectResource() == null) {
					internalEObject.eSetResource((Resource.Internal) outExtent, null);
				}
			}

			resolvedRootElements.removeAll(outExtent.getContents());
			addAllContents(outExtent.getContents(), resolvedRootElements);
		}
	}

	private static Set<EObject> getEssentialRootElements(List<? extends EObject> allRootElements) {
		Set<EObject> roots = new LinkedHashSet<EObject>();
		for (EObject e : allRootElements) {
			EObject nextRoot = e;
			while (nextRoot.eContainer() instanceof EObject && nextRoot.eContainer().eResource() == e.eResource()) {
				nextRoot = nextRoot.eContainer();
			}
			roots.add(nextRoot);
		}
		return roots;
	}

	private static void addAllContents(EList<EObject> contents, Set<EObject> elements) {
		if (contents instanceof AbstractEList<?>) {
			((AbstractEList<EObject>) contents).addAllUnique(elements);
		}
		else {
			contents.addAll(elements);
		}
	}

}
