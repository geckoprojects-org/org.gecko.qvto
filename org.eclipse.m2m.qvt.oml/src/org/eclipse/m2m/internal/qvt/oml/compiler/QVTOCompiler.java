/*******************************************************************************
 * Copyright (c) 2009, 2019 Borland Software Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Alex Paperno - bug 416584
 *     Christopher Gerking - bugs 326871, 391289, 431082, 486487, 536601, 537041
 *     Ed Willink - bug 540971
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTParsingOptions;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtEnvironmentBase;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnvFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalFileEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ExternalUnitElementsProvider;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParser;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalValidationVisitor;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalVisitorCS;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.compiler.BlackboxUnitResolver.BlackboxUnitProxy;
import org.eclipse.m2m.internal.qvt.oml.cst.ImportCS;
import org.eclipse.m2m.internal.qvt.oml.cst.UnitCS;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.AbstractQVTParser;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.SemanticException;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.PathNameCS;
import org.eclipse.ocl.lpg.AbstractLexer;


public class QVTOCompiler {

	private static final String NAMESPACE_SEP = String.valueOf(UnitProxy.NAMESPACE_SEP);

	private final Map<URI, CompiledUnit> fSource2Compiled = new HashMap<URI, CompiledUnit>();
	private final Stack<DependencyPathElement> fDependencyWalkPath = new Stack<DependencyPathElement>();
	private final IMetamodelRegistryProvider fMetamodelRegistryProvider;
	private ResourceSetImpl fExeXMIResourceSet;
	private boolean fUseCompiledXMI = false;

	/**
	 * Creates compiler that caches already compiled modules until
	 * <code>cleanup</code> is explicitly called.
	 * <p>
	 * This enables to compile individual modules separately ensuring
	 * cross-referencing among already compiled modules.
	 *
	 * @param importResolver
	 *            resolver for other moduleAST imports
	 * @param metamodelResourceSet
	 *            the resource set into which meta-model nsURI mapped to a resource location
	 *            are to be loaded. If it is <code>null</code>, a default resource set is created
	 *            automatically.
	 *            <p>
	 *            Note: The meta-models already loaded in the resource set are
	 *            reused
	 * @return the compiler instance
	 */
	public static QVTOCompiler createCompilerWithHistory(ResourceSet metamodelResourceSet) {
		metamodelResourceSet = metamodelResourceSet == null ? new ResourceSetImpl() : metamodelResourceSet;
		return new QVTOCompiler(createMetamodelRegistryProvider(metamodelResourceSet)) {
			@Override
			protected void afterCompileCleanup() {
				// do nothing as we need to cross-reference cached modules on
				// next compilation requests
			}

			@Override
			public void cleanup() {
				super.cleanup();
				afterCompileCleanup();
			}
		};
	}

	public static CompiledUnit[] compile(Set<URI> unitURIs, EPackage.Registry registry) throws MdaException {
		EList<UnitProxy> unitProxies = new BasicEList<UnitProxy>();
		for (URI importURI : unitURIs) {
			UnitProxy unit = UnitResolverFactory.Registry.INSTANCE.getUnit(importURI);
			if (unit != null) {
				unitProxies.add(unit);
			}
		}

		if(!unitProxies.isEmpty()) {
			QVTOCompiler compiler = new QVTOCompiler(registry);

			QvtCompilerOptions options = new QvtCompilerOptions();
			options.setGenerateCompletionData(true);
			return compiler.compiles(unitProxies.toArray(new UnitProxy[unitProxies.size()]), options);
		}

		return new CompiledUnit[0];
	}

	public QVTOCompiler() {
		this(EPackage.Registry.INSTANCE);
	}

	public QVTOCompiler(EPackage.Registry packageRegistry) {
		this(
//				EMFPlugin.IS_ECLIPSE_RUNNING && EMFPlugin.IS_RESOURCES_BUNDLE_AVAILABLE ?
//						new ProjectMetamodelRegistryProvider(packageRegistry) :
							new EmfStandaloneMetamodelRegistryProvider(packageRegistry)
				);
	}

	public QVTOCompiler(IMetamodelRegistryProvider metamodelRegistryProvider) {
		fMetamodelRegistryProvider = metamodelRegistryProvider;

		fExeXMIResourceSet = CompiledUnit.createResourceSet();
		if(getResourceSet() instanceof ResourceSetImpl) {
			Map<URI, Resource> uriResourceMap = ((ResourceSetImpl) getResourceSet()).getURIResourceMap();
			if(uriResourceMap != null) {
				fExeXMIResourceSet.setURIResourceMap(new HashMap<URI, Resource>(uriResourceMap));
			}
		}
	}

	public void setUseCompiledXMI(boolean flag) {
		fUseCompiledXMI = flag;
	}

	public CompiledUnit[] compiles(UnitProxy[] sources, QvtCompilerOptions options) throws MdaException {
		if(options == null) {
			options = getDefaultOptions();
		}

		CompiledUnit[] result = new CompiledUnit[sources.length];

		try {
			int i = 0;
			for (UnitProxy nextSource : sources) {
				result[i++] = compileSingleFile(nextSource, options);
			}
		} finally {
			fDependencyWalkPath.clear();
			afterCompileCleanup();

		}

		return result;
	}

	/* obsolete method re-instated to avoid GMF Tooling breakage - see Bug 540971 */
	/* @deprecated use IProgressMonitor */
	@Deprecated
	public CompiledUnit compile(UnitProxy[] sources, QvtCompilerOptions options) throws MdaException {
		return compiles(sources, options)[0];
	}

	public CompiledUnit compile(UnitProxy source, QvtCompilerOptions options) throws MdaException {
		return compiles(new UnitProxy[] { source }, options)[0];
	}

	protected CSTParseResult parse(UnitProxy source, QvtCompilerOptions options) throws ParserException {
		Reader reader = null;
		UnitCS unitCS = null;
		try {
			reader = createReader(source);

			Registry ePackageRegistry = getEPackageRegistry(source.getURI());

			QvtOperationalFileEnv env = new QvtOperationalEnvFactory(ePackageRegistry).createEnvironment(source.getURI());

			if(options.isEnableCSTModelToken()) {
				env.setOption(QVTParsingOptions.ENABLE_CSTMODEL_TOKENS, true);
			}

			QvtOperationalParser qvtParser = new QvtOperationalParser();
			unitCS = qvtParser.parse(reader, source.getName(), env);

			CSTParseResult result = new CSTParseResult();
			result.unitCS = unitCS;
			result.env = env;
			result.parser = qvtParser.getParser();
			return result;

		} catch(IOException e) {
			String ioErrorMessage = NLS.bind(CompilerMessages.sourceReadingIOError, source.getURI());
			throw new ParserException(ioErrorMessage, e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

	}

	public static Reader getContentReader(UnitProxy unit) throws IOException {
		UnitContents contents = unit.getContents();
		if(contents instanceof UnitContents.CSTContents == false) {
			throw new IllegalArgumentException("unit has no CST stream"); //$NON-NLS-1$
		}

		UnitContents.CSTContents cst = (UnitContents.CSTContents) contents;
		return cst.getContents();
	}

	protected Reader createReader(UnitProxy unit) throws IOException {
		return getContentReader(unit);
	}

	private CSTAnalysisResult analyze(CSTParseResult parseResult, UnitProxy unit,
			ExternalUnitElementsProvider externalUnitElementsProvider, QvtCompilerOptions options) {

			QvtOperationalFileEnv env = parseResult.env;
			env.setQvtCompilerOptions(options);

			CSTAnalysisResult result = new CSTAnalysisResult();
			QvtOperationalVisitorCS visitor = createAnalyzer(parseResult.parser, options);
			try {
				UnitCS unitCS = parseResult.unitCS;
				if(unitCS != null && !unitCS.getModules().isEmpty()) {
					result.moduleEnvs = visitor.visitUnitCS(unitCS, unit, env, externalUnitElementsProvider, getResourceSet());
				}
			} catch (SemanticException e) {
				env.reportError(e.getLocalizedMessage(), 0, 0);
			}
			finally {
				visitor.clear();
			}

			if (options.isReportErrors()) {

				for(QvtOperationalModuleEnv moduleEnv : result.moduleEnvs) {
	                moduleEnv.setCheckForDuplicateErrors(true);
					QvtOperationalValidationVisitor validation = new QvtOperationalValidationVisitor(moduleEnv);
					validation.visitModule(moduleEnv.getModuleContextType());
					moduleEnv.setCheckForDuplicateErrors(false);
				}
			}

			return result;
	}

    protected QvtOperationalVisitorCS createAnalyzer(AbstractQVTParser parser, QvtCompilerOptions options) {
    	return new QvtOperationalVisitorCS(parser, options);
    }

    protected void afterCompileCleanup() {
    	this.fSource2Compiled.clear();
    	this.fDependencyWalkPath.clear();
    	this.fExeXMIResourceSet.getResources().clear();
    }

	/**
	 * The main compilation method - the common entry point to the compilation
	 */
	private CompiledUnit compileSingleFile(UnitProxy source, QvtCompilerOptions options) throws MdaException {

		CompiledUnit nextResult = null;
		try {
			nextResult = doCompile(source, options);
		}
		catch (ParserException e) {
			Throwable cause = e.getCause() != null ? e.getCause() : e;
			throw new MdaException(cause);
		}

		return nextResult;
	}

	private CompiledUnit doCompile(final UnitProxy source, QvtCompilerOptions options) throws ParserException {
		try {
			List<CompiledUnit> compiledImports = null;
			DependencyPathElement dependencyElement = new DependencyPathElement(source);

			fDependencyWalkPath.push(dependencyElement);

			if(fSource2Compiled.containsKey(source.getURI())) {
				return fSource2Compiled.get(source.getURI());
			}

			if(fUseCompiledXMI) {
				CompiledUnit binXMIUnit = getCompiledExeXMIUnit(source);
				if(binXMIUnit != null) {
					fSource2Compiled.put(source.getURI(), binXMIUnit);
					return binXMIUnit;
				}
			}

			if(source instanceof BlackboxUnitProxy) {
				CompiledUnit blackbox = ((BlackboxUnitProxy) source).load(fMetamodelRegistryProvider);
				fSource2Compiled.put(source.getURI(), blackbox);

				return blackbox;
        	}


        	// perform to syntax parsing
	    	CSTParseResult parseResult = parse(source, options);

			QvtOperationalFileEnv env = parseResult.env;
			dependencyElement.importerEnv = env;

			UnitCS unitCS = parseResult.unitCS;
			UnitResolverImpl unitResolver = new UnitResolverImpl(source);
			List<ImportCS> allUnitImportsCS = parseResult.getImports();


			for (ImportCS nextImportCS : allUnitImportsCS) {
				String importQNameStr = getQualifiedName(nextImportCS);
				if(importQNameStr == null || importQNameStr.length() == 0) {
					// nothing reasonable to look for, syntax error should have been reported
					continue;
				}

				UnitProxy importedUnit = resolveImportedUnit(source, importQNameStr);
				if(importedUnit == null) {
					// report that unit was not resolved
					String notFoundMessage = NLS.bind(CompilerMessages.importedCompilationUnitNotFound,
							QvtOperationalParserUtil.getStringRepresentation(nextImportCS.getPathNameCS(), NAMESPACE_SEP));
					env.reportError(notFoundMessage, nextImportCS.getPathNameCS());

					continue;
				}

        		// check for cyclic import error condition
        		dependencyElement.currentProcessedImport = nextImportCS;

        		DependencyPathElement importerDependencyElement = findDependencyElement(importedUnit);
        		if(importerDependencyElement != null) {
	            	ImportCS importedCS = importerDependencyElement.currentProcessedImport;
        			// not cached compiled unit yet, but we got here into a cycle
	            	if(env != importerDependencyElement.importerEnv) {
	            		reportCyclicImportError(importedUnit.getURI(), source.getURI(),
	            				importedCS.getPathNameCS(), importerDependencyElement.importerEnv);
	            	}

	            	// report the cyclic problem in the opposite direction
	            	reportCyclicImportError(source.getURI(), importedUnit.getURI(), nextImportCS.getPathNameCS(), env);
	            	// skip addition to the list of imports
	            	continue;
        		}

        		CompiledUnit compiledImport = doCompile(importedUnit, options);

    			if(!compiledImport.getErrors().isEmpty()) {

    				String errorMessage	= NLS.bind(CompilerMessages.importHasCompilationError,
    						QvtOperationalParserUtil.getStringRepresentation(nextImportCS.getPathNameCS()));

    				DiagnosticChain error = env.reportError(errorMessage, nextImportCS.getPathNameCS());

    				for (Diagnostic importError : compiledImport.getErrors()) {
    					error.add(importError);
    				}
    			}

    			if(compiledImports == null) {
    				// Note: Must be unique as we process import duplicates to report problems
    				compiledImports = new UniqueEList<CompiledUnit>();
    			}

				compiledImports.add(compiledImport);

				List<String> importedUnitQName = QvtOperationalParserUtil.getSequenceOfNames(nextImportCS.getPathNameCS().getSimpleNames());
				unitResolver.addUnit(importedUnitQName, compiledImport);

			} // end of imports processing

	    	// perform CST analysis

			CSTAnalysisResult analysisResult = analyze(parseResult, source, unitResolver, options);

			if(options.isSourceLineNumbersEnabled()) {
				addSourceLineNumberInfo(parseResult.parser, analysisResult, source);
			}

			// load black-box implementation bindings
			//AST2BlackboxImplBinder.ensureImplementationBinding(source, analysisResult.modules);

			// report possible duplicate imports
			checkForDupImports(allUnitImportsCS, env);

			// get rid of parser allocated data
			env.close(); // TODO - check whether we can use dispose()
			for (QvtEnvironmentBase moduleEnv : analysisResult.moduleEnvs) {
				moduleEnv.close();
			}
			// FIXME - construct proper qualified name
			CompiledUnit result = createCompiledUnit(source, env);
			// TODO - make this optional as we not always want to carry the whole CST
			result.fUnitCST = unitCS;

			if(compiledImports != null) {
				result.setImports(compiledImports);
			}

			// put to central compilation result cache
			// TODO - better to use this one as unit resolver
			fSource2Compiled.put(source.getURI(), result);

			return result;

		} finally {
    		if (!fDependencyWalkPath.empty()) {
    			fDependencyWalkPath.pop();
    		}
    	}
	}

	private CompiledUnit getCompiledExeXMIUnit(final UnitProxy source) {
		URI xmiURI = ExeXMISerializer.toXMIUnitURI(source.getURI());
		if(URIConverter.INSTANCE.exists(xmiURI, null)) {
			// check if the bin XMI is up-to-date with the QVT source file
			Long srcTStamp = (Long)URIConverter.INSTANCE.getAttributes(source.getURI(), null).get(URIConverter.ATTRIBUTE_TIME_STAMP);
			Long binTStamp = (Long)URIConverter.INSTANCE.getAttributes(xmiURI, null).get(URIConverter.ATTRIBUTE_TIME_STAMP);
			if(binTStamp == null || (srcTStamp != null && binTStamp.equals(srcTStamp))) {
				return new CompiledUnit(fExeXMIResourceSet.getResource(xmiURI, true), fSource2Compiled);
			}
		}

		return null;
	}


	private CompiledUnit createCompiledUnit(UnitProxy unit, QvtOperationalFileEnv env) {
		// adjust QVT environment resource to point to executable XMI
		Resource resource = env.getTypeResolver().getResource();
		//resource.setURI(ExeXMISerializer.toXMIUnitURI(unit.getURI()));
		fExeXMIResourceSet.getResources().add(resource);

		List<String> qualifiedName = getQualifiedNameSegments(unit);

		ResourceSet resourceSet = new ResourceSetImpl() {
			@Override
			protected Resource delegatedGetResource(URI uri, boolean loadOnDemand) {

				// delegate to compiler's ResourceSet to prevent workspace metamodels from reloading twice
				Resource resource = QVTOCompiler.this.getResourceSet().getResource(uri, false);

				return resource != null ? resource : super.delegatedGetResource(uri, loadOnDemand);
			}
		};
		resourceSet.setPackageRegistry(env.getEPackageRegistry());

		return new CompiledUnit(qualifiedName, unit.getURI(), Collections.singletonList(env), resourceSet);
	}

	private static List<String> getQualifiedNameSegments(UnitProxy unit) {
		List<String> qualifiedName = null;
		String namespace = unit.getNamespace();
		if(namespace != null) {
			String[] segments = ResolverUtils.getNameSegments(namespace);
			qualifiedName = new ArrayList<String>(segments.length + 1);
			qualifiedName.addAll(Arrays.asList(segments));

			qualifiedName.add(unit.getName());
		} else {
			qualifiedName = Collections.singletonList(unit.getName());
		}
		return qualifiedName;
	}

	protected final EPackage.Registry getEPackageRegistry(URI context) {
		return CompilerUtils.getEPackageRegistry(context, fMetamodelRegistryProvider);
	}

	public ResourceSet getResourceSet() {
		return fMetamodelRegistryProvider.getResolutionResourceSet();
	}

	public void cleanup() {
		EmfUtil.cleanupResourceSet(getResourceSet());
	}

	private void addSourceLineNumberInfo(AbstractQVTParser parser, CSTAnalysisResult analysisResult, UnitProxy source) {
		AbstractLexer lexer = parser.getLexer();
		if (lexer != null && source.getURI() != null) {
			for (QvtOperationalModuleEnv moduleEnv : analysisResult.moduleEnvs) {
				ASTBindingHelper.createModuleSourceBinding(moduleEnv.getModuleContextType(), source.getURI(), new BasicLineNumberProvider(lexer));
			}
		}
	}

	private static String getQualifiedName(ImportCS importCS) {
		if(importCS.getPathNameCS() != null) {
			return QvtOperationalParserUtil.getStringRepresentation(importCS.getPathNameCS(), NAMESPACE_SEP);
		}
		return null;
	}

	private QvtCompilerOptions getDefaultOptions() {
		QvtCompilerOptions options = new QvtCompilerOptions();
		options.setGenerateCompletionData(false);
		return options;
	}

	private UnitProxy resolveImportedUnit(UnitProxy importingUnit, String unitQualifiedName) {
		UnitResolver resolver = importingUnit.getResolver();
		UnitProxy unit = resolver.resolveUnit(unitQualifiedName);

		if(unit == null) {
			String namespace = importingUnit.getNamespace();
			if(namespace != null && unitQualifiedName.contains(NAMESPACE_SEP) == false) {
				unit = resolver.resolveUnit(namespace + NAMESPACE_SEP + unitQualifiedName);
			}
		}

		return unit;
	}

	private void checkForDupImports(List<ImportCS> imports, QvtOperationalEnv env) {
		if (imports.size() < 2) {
			return;
		}
		// 'checkedImportTokens' is used to avoid false duplication report in case when multiple transformations reside
		// in single compilation unit (AbstractQVTParser::setupTopLevel() creates duplicated imports in such case)
		Set<Object> checkedImportTokens = new HashSet<Object>(imports.size());
		Set<Object> checkedImports = new HashSet<Object>(imports.size());
		List<ImportCS> dupImports = new LinkedList<ImportCS>();
		for(ImportCS nextImportCS : imports) {
			if(nextImportCS.getAst() != null &&
					checkedImports.contains(nextImportCS.getAst()) && !checkedImportTokens.contains(nextImportCS.getStartOffset())) {
				dupImports.add(nextImportCS);
			} else {
				checkedImports.add(nextImportCS.getAst());
				checkedImportTokens.add(nextImportCS.getStartOffset());
			}
		}

		for (ImportCS nextDupImport : dupImports) {
			PathNameCS problemCS = nextDupImport.getPathNameCS();
			env.reportWarning(NLS.bind(CompilerMessages.compilationUnitAlreadyImported,
					QvtOperationalParserUtil.getStringRepresentation(problemCS, NAMESPACE_SEP)), problemCS);
		}
	}

	private static void reportCyclicImportError(URI from, URI to, CSTNode cstNode, QvtOperationalEnv env) {
		String message = NLS.bind(CompilerMessages.cyclicImportError, from, to);
		env.reportError(message, cstNode);
	}

	static void clearITokens(CSTNode node) {
		node.setStartToken(null);
		node.setEndToken(null);

		TreeIterator<EObject> it = node.eAllContents();
		while(it.hasNext()) {
			EObject next = it.next();
			if(next instanceof CSTNode) {
				CSTNode nextCST = (CSTNode) next;
				nextCST.setStartToken(null);
				nextCST.setEndToken(null);
			}
		}
	}

	private static class UnitResolverImpl implements ExternalUnitElementsProvider {
		private final Map<List<String>, CompiledUnit> qName2CU;
		private final UnitProxy source;

		private UnitResolverImpl(UnitProxy importer) {
			this.qName2CU = new HashMap<List<String>, CompiledUnit>(5);
			this.source = importer;
		}

		void addUnit(List<String> qualifiedName, CompiledUnit unit) {
			qName2CU.put(qualifiedName, unit);
		}

		public URI getImporter() {
			return source.getURI();
		}

		public List<QvtOperationalModuleEnv> getModules(List<String> importedUnitQualifiedName) {
			if(importedUnitQualifiedName == null) {
				return Collections.emptyList();
			}

			CompiledUnit compiledUnit = qName2CU.get(importedUnitQualifiedName);
			if(compiledUnit != null) {
				return compiledUnit.getModuleEnvironments();
			}

			return Collections.emptyList();
		}
	}

	protected static class CSTParseResult {
		public UnitCS unitCS;
		public QvtOperationalFileEnv env;
		public AbstractQVTParser parser;

		public CSTParseResult() {}

		List<ImportCS> getImports() {
			if(unitCS != null) {
				return QvtOperationalParserUtil.getImports(unitCS);
			}
			return Collections.emptyList();
		}
	}

	protected static class CSTAnalysisResult {
		List<QvtOperationalModuleEnv> moduleEnvs = Collections.emptyList();
		List<ModelType> modelTypes;
	}

	private DependencyPathElement findDependencyElement(UnitProxy source) {
		for (DependencyPathElement element : fDependencyWalkPath) {
			if(source.equals(element.importer)) {
				return element;
			}
		}

		return null;
	}

	private static class DependencyPathElement {
		final UnitProxy importer;
		ImportCS currentProcessedImport;
		QvtOperationalEnv importerEnv;

		public DependencyPathElement(UnitProxy importer) {
			this.importer = importer;
		}
	}

	private static IMetamodelRegistryProvider createMetamodelRegistryProvider(ResourceSet metamodelResourceSet) {
//		if(EMFPlugin.IS_ECLIPSE_RUNNING && EMFPlugin.IS_RESOURCES_BUNDLE_AVAILABLE) {
//			return new ProjectMetamodelRegistryProvider(metamodelResourceSet);
//		}

		return new EmfStandaloneMetamodelRegistryProvider(metamodelResourceSet.getPackageRegistry());
	}
}
