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
 *     Christopher Gerking - bugs 289982, 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ocl.transformations;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxProvider;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxUnitDescriptor;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxException;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxUnit;
import org.eclipse.m2m.internal.qvt.oml.blackbox.LoadContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.OperationMatcher;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContext;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.ocl.OclQvtoPlugin;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandlerAdapter;

public class LegacyNativeLibraryProviderAdapter extends BlackboxProvider {
	
	private Map<String, BlackboxUnitDescriptor> fDescriptorMap;
	private final Map<LibraryDescriptor, BlackboxUnit> fBlackboxUnits = new LinkedHashMap<LibraryDescriptor, BlackboxUnit>();
	
	public LegacyNativeLibraryProviderAdapter() {
		super();
	}

	@Override
	public Collection<BlackboxUnitDescriptor> getUnitDescriptors(ResolutionContext loadContext) {
		return getDescriptorMap().values();
	}
	
	@Override
	public BlackboxUnitDescriptor getUnitDescriptor(String qualifiedName, ResolutionContext resolutionContext) {
		return getDescriptorMap().get(qualifiedName);		
	}
		
	@Override
	public void cleanup() {
		fBlackboxUnits.clear();
		fDescriptorMap = null;
	}
	
	private Map<String, BlackboxUnitDescriptor> getDescriptorMap() {
		if (fDescriptorMap != null) {
			return fDescriptorMap;
		}
		
		LibrariesRegistry registry = OclQvtoPlugin.getDefault().getLibrariesRegistry();

		fDescriptorMap = new LinkedHashMap<String, BlackboxUnitDescriptor>(registry.getLibraries().size());
		for (final Library lib : registry.getLibraries()) {
			fDescriptorMap.put(lib.getId(), new LibraryDescriptor(lib));			
		}
		
		return fDescriptorMap;
	}
	
	
	private class LibraryDescriptor extends BlackboxUnitDescriptor {

		private final Library fLibrary;
		private final Map<String, List<EOperation>> fDefinedOperations;
		
		protected LibraryDescriptor(Library library) {
			super(LegacyNativeLibraryProviderAdapter.this, library.getId());
			fLibrary = library;
			fDefinedOperations = new LinkedHashMap<String, List<EOperation>>();
		}
				
		@Override
		public String getDescription() {
			return fLibrary.getLibraryClassName();
		}
		
		public Collection<CallHandler> getBlackboxCallHandler(ImperativeOperation imperativeOp, QvtOperationalModuleEnv env) {
			if (!env.getImportedNativeLibs().isEmpty()) {
				Set<String> importedLibs = env.getImportedNativeLibs().get(getURI());
				if (!importedLibs.contains(fLibrary.getId())) {
					return Collections.emptyList();
				}
			}
			
			List<EOperation> listOp = fDefinedOperations.get(imperativeOp.getName());
			if (listOp == null) {
				return Collections.emptyList();
			}
			
			Collection<CallHandler> result = Collections.emptyList();
			for (EOperation libraryOp : listOp) {
				if (OperationMatcher.matchOperation(env, imperativeOp, libraryOp)) {
					if (result.isEmpty()) {
						result = new LinkedList<CallHandler>();
					}
					result.add(CallHandlerAdapter.getDispatcher(libraryOp));
				}
			}
			
			return result;
		}
		
		public Collection<CallHandler> getBlackboxCallHandler(OperationalTransformation operationalTrans, QvtOperationalModuleEnv env) {
			if (!env.getImportedNativeLibs().isEmpty()) {
				Set<String> importedLibs = env.getImportedNativeLibs().get(getURI());
				if (!importedLibs.contains(fLibrary.getId())) {
					return Collections.emptyList();
				}
			}
			
			List<EOperation> listOp = fDefinedOperations.get(operationalTrans.getName());
			if (listOp == null) {
				return Collections.emptyList();
			}
			
			Collection<CallHandler> result = Collections.emptyList();
			for (EOperation libraryOp : listOp) {
				if (OperationMatcher.matchOperation(env, operationalTrans, libraryOp)) {
					if (result.isEmpty()) {
						result = new LinkedList<CallHandler>();
					}
					result.add(CallHandlerAdapter.getDispatcher(libraryOp));
				}
			}
			
			return result;
		}
		
		@Override
		public BlackboxUnit load(LoadContext context) throws BlackboxException {
			
			if (fBlackboxUnits.containsKey(this)) {
				return fBlackboxUnits.get(this);
			}
			
			try {
				BlackboxUnit compilationUnit = createBlackboxUnit(LegacyNativeLibSupport.INSTANCE.defineLibrary(fLibrary, fDefinedOperations));			
				fBlackboxUnits.put(this, compilationUnit);
				return compilationUnit;
			} catch (LibraryCreationException e) {
				fBlackboxUnits.put(this, null);
				throw new BlackboxException(e.getMessage(), e);			
			}
		}
		
	}	
}
