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
 *     Christopher Gerking - bug 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompilerMessages;
import org.eclipse.m2m.internal.qvt.oml.compiler.EmfStandaloneMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.compiler.QVTOCompiler;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitProxy;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolverFactory;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.osgi.util.NLS;

public class DeployedQvtModule extends QvtModule {

	private Module myModule;
	private CompiledUnit myUnit;
	private ResourceSet myCompilationRs;
	private final URI moduleUri;
	private final EPackage.Registry packageRegistry;

	public DeployedQvtModule(URI qvtModuleUri, EPackage.Registry packageRegistry) throws MdaException {
		if(qvtModuleUri == null || qvtModuleUri.segmentCount() == 0) {
			throw new MdaException(NLS.bind(Messages.TransformationUtil_InvalidUri,
					qvtModuleUri == null ? String.valueOf(null) : qvtModuleUri.toString()));
		}
		this.moduleUri = qvtModuleUri;
		this.packageRegistry = packageRegistry;
	}

	protected IMetamodelRegistryProvider creatMetamodelRegistryProvider() {
		if (packageRegistry != null) {
			return new EmfStandaloneMetamodelRegistryProvider(packageRegistry);
		}
		return new EmfStandaloneMetamodelRegistryProvider();
	}

	@Override
	public Module getModule() throws MdaException {
		if (myModule == null) {
			UnitProxy srcUnit = UnitResolverFactory.Registry.INSTANCE.getUnit(moduleUri);
			if (srcUnit == null) {
				throw new MdaException(NLS.bind(CompilerMessages.moduleNotFound, moduleUri));
			}

			QVTOCompiler qvtCompiler = new QVTOCompiler(creatMetamodelRegistryProvider());

			QvtCompilerOptions options = getQvtCompilerOptions();
			if (options == null) {
				options = new QvtCompilerOptions();
				options.setGenerateCompletionData(false);
			}

			myUnit = qvtCompiler.compile(srcUnit, options);

			if (!options.isModuleWithErrorAllowed()) {
				checkModuleErrors(myUnit);
			}

			// FIXME - we should add support of uri fragment, being the name of the referred module
			myModule = myUnit.getModules().get(0);
			myCompilationRs = myUnit.getResourceSet();
		}

		return myModule;
	}

	@Override
	public ResourceSet getResourceSet() {
		return myCompilationRs;
	}

	@Override
	public void cleanup() {
		if (myCompilationRs != null) {
			EmfUtil.cleanupResourceSet(myCompilationRs);
		}
	}

	@Override
	public CompiledUnit getUnit() throws MdaException {
		getModule();
		return myUnit;
	}

	@Override
	public String toString() {
		return "deployed:/" + moduleUri; //$NON-NLS-1$
	}
}
