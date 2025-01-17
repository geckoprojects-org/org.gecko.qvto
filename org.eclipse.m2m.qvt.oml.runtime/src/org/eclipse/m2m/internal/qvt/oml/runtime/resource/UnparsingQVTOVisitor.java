/*******************************************************************************
 * Copyright (c) 2016, 2018 Uwe Ritzmann and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Uwe Ritzmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtEnvironmentBase;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.QvtKeywords;
import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;
import org.eclipse.m2m.internal.qvt.oml.expressions.ConstructorBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.util.QVTOperationalVisitor;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AltExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AssertExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AssignExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.BlockExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.BreakExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.CatchExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ComputeExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ContinueExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictLiteralExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictLiteralPart;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ForExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeExpression;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeIterateExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeLoopExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.InstantiationExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListLiteralExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.LogExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.OrderedTupleLiteralExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.OrderedTupleLiteralPart;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.RaiseExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ReturnExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.SeverityKind;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.SwitchExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.TryExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.UnlinkExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.UnpackExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.VariableInitExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.WhileExp;
import org.eclipse.ocl.ecore.AnyType;
import org.eclipse.ocl.ecore.BagType;
import org.eclipse.ocl.ecore.CallExp;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.FeatureCallExp;
import org.eclipse.ocl.ecore.InvalidType;
import org.eclipse.ocl.ecore.LiteralExp;
import org.eclipse.ocl.ecore.LoopExp;
import org.eclipse.ocl.ecore.NavigationCallExp;
import org.eclipse.ocl.ecore.NumericLiteralExp;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.ocl.ecore.OppositePropertyCallExp;
import org.eclipse.ocl.ecore.OrderedSetType;
import org.eclipse.ocl.ecore.PrimitiveLiteralExp;
import org.eclipse.ocl.ecore.PrimitiveType;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.ecore.SequenceType;
import org.eclipse.ocl.ecore.SetType;
import org.eclipse.ocl.ecore.TemplateParameterType;
import org.eclipse.ocl.ecore.TupleType;
import org.eclipse.ocl.ecore.TypeType;
import org.eclipse.ocl.ecore.VoidType;
import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.CollectionLiteralPart;
import org.eclipse.ocl.expressions.CollectionRange;
import org.eclipse.ocl.expressions.EnumLiteralExp;
import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.InvalidLiteralExp;
import org.eclipse.ocl.expressions.IterateExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.LetExp;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StateExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.expressions.UnspecifiedValueExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.ExpressionInOCL;

public class UnparsingQVTOVisitor implements QVTOperationalVisitor<Object>
{
	public void unparseModules(EList<Module> modules)
	{
		EList<ModuleImport> moduleImportList = getListOfModuleImports(modules);
		unparseModuleImportsForCompilationUnit(moduleImportList);
		
		EList<ModelType> modelTypeList = getListOfModelTypes(modules);
		unparseModelTypesForCompilationUnit(modelTypeList);
		
		for (Module module : modules)
		{
			unparseModule(module);
			addEmptyLine();
		}
	}

	private EList<ModuleImport> getListOfModuleImports(EList<Module> modules)
	{
		EList<ModuleImport> moduleImportList = new BasicEList<ModuleImport>();

		for (Module module : modules)
		{
			for (ModuleImport moduleImport : module.getModuleImport())
			{
				if (moduleImport == null || moduleImport.getImportedModule() == null) {
					continue;
				}
				if (!modules.contains(moduleImport.getImportedModule()))
				{
					if (!moduleImportList.contains(moduleImport))
					{
						moduleImportList.add(moduleImport);
					}
				}
			}			
		}

		return moduleImportList;
	}

	
	public Object visitModule(Module module)
	{
		unparseModule(module);
		return null;
	}

	public void unparseModule(Module module)
	{
		if ( module instanceof OperationalTransformation )
		{
			unparseOperationalTransformation((OperationalTransformation)module);
		}
		else if ( module instanceof Library )
		{
			unparseLibrary((Library)module);
		}
		else
		{
			addText("/*___unknownModuleType___*/");
		}
	}

	private void unparseOperationalTransformation(OperationalTransformation operationalTransformation)
	{
		unparseTags(operationalTransformation);
		unparseOperationalTransformationSignature(operationalTransformation);
		unparseModuleImportsForSignature(operationalTransformation);
		addText("{");
		addLineBreak();
		unparseIntermediateClasses(operationalTransformation);
		unparseModuleProperties(operationalTransformation);
		unparseIntermediateProperties(operationalTransformation);
		unparseModuleOperations(operationalTransformation);
		addText("}");
		addLineBreak();
	}

	public Object visitLibrary(Library library)
	{
		unparseLibrary(library);
		return null;
	}

	private void unparseLibrary(Library library)
	{
		unparseTags(library);
		unparseLibrarySignature(library);
		unparseModuleImportsForSignature(library);
		addText("{");
		addLineBreak();
		unparseModuleProperties(library);
		unparseModuleOperations(library);
		addText("}");
		addLineBreak();
	}

	private EList<ModelType> getListOfModelTypes(EList<Module> modules)
	{
		Set<String> handledMetamodels = new HashSet<String>();
		EList<ModelType> modelTypeList = new BasicEList<ModelType>();
		for (Module module : modules)
		{
			EList<ModelType> usedModelTypes = module.getUsedModelType();
			for (ModelType modelType : usedModelTypes)
			{
				String modelTypeKey = modelType.getName()+":"+modelType.getMetamodel().get(0).getNsURI();
				if ( !handledMetamodels.contains(modelTypeKey) )
				{
					modelTypeList.add(modelType);
					handledMetamodels.add(modelTypeKey);
				}
			}
		}
		return modelTypeList;
	}
	
	private void unparseModelTypesForCompilationUnit(EList<ModelType> usedModelTypes)
	{
		for (ModelType modelType : usedModelTypes)
		{
			unparseModelType(modelType);
		}

		if ( usedModelTypes.size() > 0 ) 
			addLineBreak();
	}
	
	public Object visitModelType(ModelType modelType)
	{
		unparseModelType(modelType);
		return null;
	}

	private void unparseModelType(ModelType modelType)
	{
		String instanceClassName = modelType.getInstanceClassName();
		if ( "org.eclipse.m2m.internal.qvt.oml.evaluator.ModelInstance".equals(instanceClassName) )
		{
			String name = modelType.getName();
			String conformance = modelType.getConformanceKind();
			String conformanceText = conformance != null ? " \"" + conformance +"\"" : "";
			String nsURI = modelType.getMetamodel().get(0).getNsURI();
			addText("modeltype " + name + conformanceText + " uses \"" + nsURI + "\";");
			addLineBreak();
			modelTypeMap.put(modelType.getMetamodel().get(0),name);
		}
		else
		{
			addText("/*___unknownModelType___*/");
			addLineBreak();			
		}
	}

	private void unparseModuleImportsForCompilationUnit(EList<ModuleImport> moduleImportList)
	{
		for (ModuleImport moduleImport : moduleImportList)
		{
			Module importedModule = moduleImport.getImportedModule();
			
			if ( importedModule instanceof Library )
			{
				unparseLibraryImport((Library)importedModule);
			
			}
			else if ( importedModule instanceof OperationalTransformation )
			{
				unparseTransformationImport((OperationalTransformation)importedModule);
			}
			else
			{
				addText("/*___importOfUnknowModuleType___*/");
				addLineBreak();
			}			
		}		

		if ( moduleImportList.size() > 0 ) 
			addLineBreak();
	}

	public Object visitModuleImport(ModuleImport moduleImport)
	{
		return null;
	}

	private void unparseModuleImportsForSignature(Module module)
	{
		EList<ModuleImport> moduleImportList = module.getModuleImport();
		
		for (ModuleImport moduleImport : moduleImportList)
		{
			unparseModuleImportForSignature(moduleImport);
			addLineBreak();
		}		
	}

	private void unparseModuleImportForSignature(ModuleImport moduleImport)
	{
		ImportKind importKind = moduleImport.getKind();		
		String kindToken = "imports";
		if ( ImportKind.ACCESS.equals(importKind) ) kindToken = "access";
		if ( ImportKind.EXTENSION.equals(importKind) ) kindToken = "extends";

		Module importedModule = moduleImport.getImportedModule();
		
		String libraryText = importedModule instanceof Library ? " library ": " ";
		
		String moduleName = importedModule.getName();
		moduleName = moduleName.replaceAll("\\.", "::");
		addText(kindToken + libraryText + moduleName);
		
		EList<ModelType> bindings = moduleImport.getBinding();
		if ( bindings != null && bindings.size() > 0 )
		{
			addText("(");
			for (Iterator<ModelType> modelTypeIterator = bindings.iterator(); modelTypeIterator.hasNext();)
			{
				addText(modelTypeIterator.next().getName());
				if ( modelTypeIterator.hasNext() )
				{
					addText(", ");
				}
			}
			addText(")");
		}
	}

	private Set<String> importedLibraries = new HashSet<String>();
	
	private void unparseLibraryImport(Library library)
	{
		String libraryName = library.getName();

		Resource resource = library.eResource();
		String resourceURI = resource.getURI().toString();
		
		if (resourceURI.startsWith("qvto://blackbox/"))
		{
			String unitName = resourceURI.replace("qvto://blackbox/", "");
			libraryName = unitName;
		}
		
		if ( ! importedLibraries.contains(libraryName) )
		{
			addText("import library " + libraryName + ";");
			addLineBreak();
			importedLibraries.add(libraryName);
		}

	}
	
	private Set<String> importedTransformation = new HashSet<String>();
	
	private void unparseTransformationImport(OperationalTransformation transformation)
	{
		String transformationName = transformation.getName();

		Resource resource = transformation.eResource();
		String resourceURI = resource.getURI().toString();
		
		if (resourceURI.startsWith("qvto://blackbox/"))
		{
			String unitName = resourceURI.replace("qvto://blackbox/", "");
			transformationName = unitName;
		}
		
		if ( ! importedTransformation.contains(transformationName) )
		{
			addText("import " + transformationName + ";");
			addLineBreak();
			importedTransformation.add(transformationName);
		}
	}
	
	private void unparseTags(Module module)
	{
		EList<EAnnotation> ownedTags = module.getOwnedTag();
		for ( EAnnotation eAnnotation : ownedTags )
		{
			unparseTag(eAnnotation);
		}
	}
	
	private void unparseTag(EAnnotation annotation)
	{
		// <tag> ::= 'tag' <tagid> <scoped_identifier> ('=' <tagvalue>)? ';'
		// <tagid> ::= <STRING>
		// <tagvalue> :: <expression>
		String tagidText = (String)	annotation.getDetails().keySet().toArray()[0];
		if ( tagidText.equals(QvtOperationalEnv.TAG_ALIAS) )
		{
			unparseAliasTag(annotation);
		}
		else
		{
			defaultUnparseTag(annotation);
		}
	}

	private void unparseAliasTag(EAnnotation annotation)
	{
		String tagidText = (String)	annotation.getDetails().keySet().toArray()[0];
		String newNameText = (String) annotation.getDetails().values().toArray()[0];
		EObject referencedObject = annotation.getReferences().get(0);
		if ( referencedObject instanceof EStructuralFeature )
		{
			EStructuralFeature referencedFeature = (EStructuralFeature) referencedObject;
			EClass referencedContainingClass = referencedFeature.getEContainingClass();
			String originalNameText = escapeKeywordName(referencedFeature.getName());
			String fqnText = constructFullyQualifiedType(referencedContainingClass);
			addText("tag ");
			addText("\"" + tagidText + "\" " + fqnText + "::" + originalNameText + " = '" + newNameText + "';");
			addLineBreak();			
		}
	}

	private void defaultUnparseTag(EAnnotation annotation)
	{
		String tagidText = (String)	annotation.getDetails().keySet().toArray()[0];
		String valueText = (String) annotation.getDetails().values().toArray()[0];
		EObject referencedObject = annotation.getReferences().get(0);
		if ( referencedObject instanceof EClassifier )
		{
			EClassifier referencedClassifier = (EClassifier) referencedObject;
			String fqnText = constructFullyQualifiedType(referencedClassifier);
			addText("tag ");
			addText("\"" + tagidText + "\" " + fqnText + " = '" + valueText + "';");
			addLineBreak();			
		}
		else if ( referencedObject instanceof EStructuralFeature )
		{
			EStructuralFeature referencedFeature = (EStructuralFeature) referencedObject;
			EClass referencedContainingClass = referencedFeature.getEContainingClass();
			String nameText = escapeKeywordName(referencedFeature.getName());
			String fqnClassText = constructFullyQualifiedType(referencedContainingClass);
			addText("tag ");
			addText("\"" + tagidText + "\" " + fqnClassText + "::" + nameText + " = '" + valueText + "';");
			addLineBreak();			
		}
		else
		{
			//TODO
		}
	}

	private void unparseType(EClassifier type)
	{
		if ( type != null )
			addText(constructFullyQualifiedType(type));
	}
	
	private void unparseHelperContext(VarParameter helperContext)
	{
		if ( helperContext != null )
		{
			String contextString = constructFullyQualifiedType(helperContext.getType());
			addText(contextString + "::");
		}
	}

	private Map<EPackage,String> modelTypeMap = new HashMap<EPackage,String>();
	
	private String constructFullyQualifiedType(EClassifier type)
	{
		if ( type instanceof AnyType )
			return constructFullyQualifiedAnyType((AnyType)type);
		if ( type instanceof EClass )
			return constructFullyQualifiedEClass((EClass)type);
		if ( type instanceof EDataType )
			return constructFullyQualifiedEDataType((EDataType)type);
		if ( type instanceof InvalidType )
			return constructFullyQualifiedInvalidType((InvalidType)type);
		if ( type instanceof TemplateParameterType )
			return constructFullyQualifiedTemplateParameterType((TemplateParameterType)type);
		if ( type instanceof TypeType )
			return constructFullyQualifiedTypeType((TypeType)type);
		if ( type instanceof VoidType )
			return constructFullyQualifiedVoidType((VoidType)type);
		return "/*___UnknownType___*/";
	}

	private String constructFullyQualifiedAnyType(AnyType type)
	{
		return "OclAny";
	}

	private String constructFullyQualifiedEClass(EClass type)
	{
		if ( type instanceof TupleType )
			return constructFullyQualifiedTupleType((TupleType)type);
		
		if ( IntermediateClassFactory.isIntermediateClass(type) )
			return constructFullyQualifiedIntermediateClass(type);
		
		return constructFullyQualifiedTypeAsPackagePrefixAndName(type);
	}

	private String constructFullyQualifiedEDataType(EDataType type)
	{
		if ( type instanceof CollectionType )
			return constructFullyQualifiedCollectionType((CollectionType)type);
		if ( type instanceof PrimitiveType )
			return constructFullyQualifiedPrimitiveType((PrimitiveType)type);
		if ( type instanceof TupleType )
			return constructFullyQualifiedTupleType((TupleType)type);
		
		return constructFullyQualifiedTypeAsPackagePrefixAndName(type);
	}

	private String constructFullyQualifiedCollectionType(CollectionType collectionType)
	{
		if ( collectionType instanceof DictionaryType )
			return constructFullyQualifiedDictionaryType((DictionaryType)collectionType);

		String collectionName = "Collection";
		if ( collectionType instanceof ListType )
			collectionName = "List";
		else if ( collectionType instanceof SequenceType )
			collectionName = "Sequence";
		else if ( collectionType instanceof OrderedSetType )
			collectionName = "OrderedSet";
		else if ( collectionType instanceof SetType )
			collectionName = "Set";
		else if ( collectionType instanceof BagType )
			collectionName = "Bag";
		
		String elementTypeText = constructFullyQualifiedType(collectionType.getElementType());

		return collectionName + "(" + elementTypeText + ")";
	}

	private String constructFullyQualifiedDictionaryType(DictionaryType dictionaryType)
	{
		String keyTypeText = constructFullyQualifiedType(dictionaryType.getKeyType());
		String elementTypeText = constructFullyQualifiedType(dictionaryType.getElementType());
		return "Dict(" + keyTypeText + "," + elementTypeText + ")";
	}
	
	private String constructFullyQualifiedPrimitiveType(PrimitiveType primitiveType)
	{
		return primitiveType.getName();
	}
	
	private String constructFullyQualifiedTupleType(TupleType tupleType)
	{
		StringBuffer featuresText = new StringBuffer();
		EList<EStructuralFeature> tupleFeatures = tupleType.getEStructuralFeatures();
		for (Iterator<EStructuralFeature> featureIterator = tupleFeatures.iterator(); featureIterator.hasNext();)
		{
			EStructuralFeature tupleFeature = featureIterator.next();
			String nameText = tupleFeature.getName();
			String fullTypeText = constructFullyQualifiedType(tupleFeature.getEType());
			featuresText.append(nameText + ":" + fullTypeText);
			if ( featureIterator.hasNext() )
				featuresText.append(",");
		}
		return "Tuple(" + featuresText.toString() + ")";
	}
	
	private String constructFullyQualifiedInvalidType(InvalidType type)
	{
		return "OclInvalid";
	}

	private String constructFullyQualifiedTemplateParameterType(TemplateParameterType type)
	{
		return "/*___TemplateParameterType___*/";//TODO
	}

	private String constructFullyQualifiedTypeType(TypeType type)
	{
		return "/*___TypeType___*/";//TODO
	}

	private String constructFullyQualifiedVoidType(VoidType type)
	{
		return "OclVoid";
	}
	
	private String constructFullyQualifiedIntermediateClass(EClassifier intermediateClass)
	{
		return intermediateClass.getName();
	}	
	
	private String constructFullyQualifiedTypeAsPackagePrefixAndName(EClassifier type)
	{
		String name = type.getName();
		String packagePrefix = constructPackagePrefix(type.getEPackage());

		return packagePrefix + name;
	}

	private String constructPackagePrefix(EPackage pakkage)
	{
		if ( pakkage instanceof Library )
			return constructPackagePrefixForLibrary((Library)pakkage);
			
		String packagePrefix = "";
		while (pakkage != null)
		{
			if (pakkage.getESuperPackage() != null)
			{
				packagePrefix = pakkage.getName() + "::" + packagePrefix;
				pakkage = pakkage.getESuperPackage();
			}
			else
			{
				String modelTypeName = modelTypeMap.get(pakkage);
				if ( modelTypeName != null )
				{
					packagePrefix = modelTypeName + "::" + packagePrefix;
				}
				pakkage = null;
			}
		}
		return packagePrefix;
	}

	private String constructPackagePrefixForLibrary(Library library)
	{
		String packagePrefix = library.getName() + "::";
		return packagePrefix;
	}

	private String constructContextString(VarParameter context)
	{
		String contextString = context == null ? "" : constructFullyQualifiedType(context.getType()) + "::";
		return contextString;
	}

	private void unparseOperationalTransformationSignature(OperationalTransformation transformation)
	{
		if ( transformation.isIsBlackbox() )
		{
			addText("blackbox ");
		}
		
		addText("transformation " + transformation.getName());
		
		EList<ModelParameter> modelParameterList = removeAutogenParameter(transformation.getModelParameter());
		
		addText("(");
		for (Iterator<ModelParameter> modelParameterIterator = modelParameterList.iterator(); modelParameterIterator.hasNext();)
		{
			unparseTransformationParameter(modelParameterIterator.next());
			if ( modelParameterIterator.hasNext() )
				addText(", ");
		}
		addText(")");

		addLineBreak();
	}

	private EList<ModelParameter> removeAutogenParameter(EList<ModelParameter> allModelParameter)
	{
		EList<ModelParameter> modelParameterList = new BasicEList<ModelParameter>(allModelParameter);

		for (int i = 0; i < modelParameterList.size(); )
		{
			ModelParameter modelParameter = modelParameterList.get(i);
			
			EAnnotation annotation = modelParameter.getEAnnotation(QvtOperationalParserUtil.QVT_AUTOGEN_MODELPARAM_EXPRESSION_URI);
			
			if ( annotation != null )
			{
				modelParameterList.remove(modelParameter);
			}
			else
			{
				i++;
			}
		}
		return modelParameterList;
	}
	
	private void unparseTransformationParameter(ModelParameter modelParameter)
	{
		String nameText = modelParameter.getName() + ":";
		if ( modelParameter.getName().startsWith(QvtOperationalEnv.GENERATED_NAME_SPECIAL_PREFIX) )
		{
			nameText = "/*" + nameText + "*/";
		}
		DirectionKind directionKind = modelParameter.getKind();
		String directionText = directionKind != null ? directionKind.getName() : "/*___directionKindWasNull___*/";
		EParameter representedParameter = modelParameter.getRepresentedParameter();
		String modelTypeText;
		if ( representedParameter == null )
		{
			ModelType modelType = (ModelType) modelParameter.getEType();
			modelTypeText = modelType != null ? modelType.getName() : "/*___modelTypeWasNull___*/";
		}
		else
		{
			ModelType modelType = (ModelType) representedParameter.getEType();
			modelTypeText = modelType != null ? modelType.getName() : "/*___modelTypeOfRepresentedParameterWasNull___*/";
		}
		addText(directionText + " " + nameText + modelTypeText);
	}

	private void unparseIntermediateClasses(OperationalTransformation transformation)
	{
		EList<EClass> intermediateClasses = transformation.getIntermediateClass();
		for (Iterator<EClass> classIterator = intermediateClasses.iterator(); classIterator.hasNext();)
		{
			EClass eClass = classIterator.next();
			unparseIntermediateClassOrException(eClass);
		}
	}
	
	private void unparseIntermediateClassOrException(EClass clazz)
	{
		if ( clazz == null ) return;
		
		if ( !checkIntermediateException(clazz) )
		{
			unparseIntermediateClass(clazz);
		}
		else
		{
			unparseIntermediateException(clazz);	
		}
	}
	
	private boolean checkIntermediateException(EClass clazz)
	{
		if ( clazz == null ) return false;
		
		EList<EClass> superTypes = clazz.getESuperTypes();
		
		if ( superTypes == null || superTypes.size() == 0 ) return false;
		
		for (Iterator<EClass> typesIterator = superTypes.iterator(); typesIterator.hasNext();)
		{
			EClass eClass = typesIterator.next();
			if (!eClass.getName().equals("Exception")) continue;
			if (!eClass.getEPackage().getName().equals("Stdlib")) continue;
			return true;
		}
		return false;
	}

	private void unparseIntermediateClass(EClass clazz)
	{
		if ( clazz == null ) return;
		
		addText("intermediate class " + clazz.getName());
		
		unparseIntermediateClassSupertypes(clazz.getESuperTypes());

		addLineBreak();
		addTextAndIndent("{");
		addLineBreak();
		
		unparseIntermediateClassProperties(clazz.getEAttributes());
		unparseIntermediateClassReferences(clazz.getEReferences());
		
		unindentAndAddText("}");
		addEmptyLine();
	}

	private void unparseIntermediateException(EClass exceptionClass)
	{
		if ( exceptionClass == null ) return;
		
		addText("exception " + exceptionClass.getName());
		unparseIntermediateClassSupertypesWithoutExceptionClass(exceptionClass.getESuperTypes());
		addText(" {}");
		addLineBreak();
	}

	private void unparseIntermediateClassSupertypesWithoutExceptionClass(EList<EClass> superTypes)
	{
		EList<EClass> filteredSuperTypes = removeExceptionClassFromClassList(superTypes);
		unparseIntermediateClassSupertypes(filteredSuperTypes);
	}

	private EList<EClass> removeExceptionClassFromClassList(EList<EClass> superTypes)
	{
		EList<EClass> filteredClassList = new BasicEList<EClass>();
		for (Iterator<EClass> iterator = superTypes.iterator(); iterator.hasNext();)
		{
			EClass superType = iterator.next();
			if ( superType.getName().equals("Exception") && superType.getEPackage().getName().equals("Stdlib"))
			{
				//filter StdLib::Exception
			}
			else
			{
				filteredClassList.add(superType);
			}
		}
		return filteredClassList;
	}

	private void unparseIntermediateClassSupertypes(EList<EClass> superTypes)
	{
		if ( superTypes == null || superTypes.size() == 0 ) return;
		
		addText(" extends ");
		
		for (Iterator<EClass> typesIterator = superTypes.iterator(); typesIterator.hasNext();)
		{
			EClass eClass = typesIterator.next();
			unparseType(eClass);
			if ( typesIterator.hasNext() )
				addText(", ");			
		}
	}

	private void unparseIntermediateClassProperties(EList<EAttribute> properties)
	{
		if ( properties == null ) return;
		
		for (Iterator<EAttribute> propertyIterator = properties.iterator(); propertyIterator.hasNext();)
		{
			EAttribute property = propertyIterator.next();
			unparseIntermediateClassProperty(property);		
		}
	}

	private void unparseIntermediateClassProperty(EAttribute property)
	{
		String assignmentOp = " = ";
		
		String idText = property.isID() ? "<<id>> " : "";
		
		EAnnotation staticAnnotation = property.getEAnnotation(QvtOperationalParserUtil.QVT_NAMESPACE_URI + "/static");
		String staticText = staticAnnotation != null ? "static " : "";
		
		String readonlyText = property.isChangeable() ? "" : "readonly ";

		String variableName = property.getName();		
		
		addText(idText + staticText + readonlyText + variableName + " : ");
		unparseType(property.getEType());
		if ( property.isMany() )
		{
			int lowerBound = property.getLowerBound();
			String lowerBoundText = (lowerBound != 0) ? String.valueOf(lowerBound) + " .. " : "";
			int upperBound = property.getUpperBound();
			String upperBoundText = (upperBound == -1 ) ? "*" : String.valueOf(upperBound);
			addText("[" + lowerBoundText + upperBoundText + "]");
		}
		else
		{
			if ( property.getLowerBound() == 1 )
				addText("[1]");
		}
		if ( property.isOrdered() )
			addText(" ordered");
				
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = QvtOperationalParserUtil.getInitExpression(property); 
		if ( initExpression != null )
		{
			addText(assignmentOp);
			unparseOCLExpression(initExpression);
		}
		addText(";");
		addLineBreak();		
	}

	private void unparseIntermediateClassReferences(EList<EReference> references)
	{
		if ( references == null ) return;
		
		for (Iterator<EReference> referenceIterator = references.iterator(); referenceIterator.hasNext();)
		{
			EReference reference = referenceIterator.next();
			unparseIntermediateClassReference(reference);
		}
	}

	private void unparseIntermediateClassReference(EReference reference)
	{
		String assignmentOp = " = ";
		
		String variableName = reference.getName();

		EAnnotation staticAnnotation = reference.getEAnnotation(QvtOperationalParserUtil.QVT_NAMESPACE_URI + "/static");
		String staticText = staticAnnotation != null ? "static " : "";
		
		String readonlyText = reference.isChangeable() ? "" : "readonly ";

		if ( reference.isContainment() )
		{
			addText("composes ");
		}
		else
		{
			addText("references ");
		}
		addText(staticText + readonlyText + variableName + " : ");
		unparseType(reference.getEType());
		if ( reference.isMany() )
			addText("[*]");
		if ( reference.isOrdered() )
			addText(" ordered");
		
		EReference opposite = reference.getEOpposite();
		if ( opposite != null )
		{
			addText(" opposites " + opposite.getName());
		}
		
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = QvtOperationalParserUtil.getInitExpression(reference); 
		if ( initExpression != null )
		{
			addText(assignmentOp);
			unparseOCLExpression(initExpression);
		}
		addText(";");
		addLineBreak();		
	}

	private void unparseLibrarySignature(Library library)
	{
		addText("library " + library.getName() + "(");
//		for (Iterator<ETypeParameter> iterator = library.getETypeParameters().iterator(); iterator.hasNext();)
//		{
//			ETypeParameter typeParameter = iterator.next();
//			ModelType modelType = null;//FIXME visitLibrarySignature
//			addText(modelType.getName());
//			if ( iterator.hasNext() )
//			{
//				addText(", ");
//			}
//		}
		addText(")");
		addLineBreak();
	}
	
	private void unparseModuleProperties(Module module)
	{
		EList<EStructuralFeature> configurationProperties = module.getConfigProperty();
		
		EList<EStructuralFeature> properties = module.getEStructuralFeatures();
		
		boolean hadProperty = false;
		
		for ( EStructuralFeature structuralFeature : properties )
		{
			if ( structuralFeature instanceof EAttribute )
			{
				EAttribute attribute = (EAttribute) structuralFeature;
				unparseProperty(attribute,configurationProperties.contains(attribute));
				hadProperty = true;
			}
			else if ( structuralFeature instanceof EReference )
			{
				EReference reference = (EReference) structuralFeature;
				unparseProperty(reference,configurationProperties.contains(reference));
				hadProperty = true;
			}
		}		

		if ( hadProperty ) 
			addLineBreak();
	}
	
	private void unparseProperty(EAttribute property, boolean isConfigurationProperty)
	{
		String propertyName = property.getName();
		addText((isConfigurationProperty ? "configuration " : "") +  "property " + propertyName + ": ");
		unparseType(property.getEType());
		
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = QvtOperationalParserUtil.getInitExpression(property);
		if ( initExpression != null )
		{
			addText(" = ");
			unparseOCLExpression(initExpression);
		}
		
		addText(";");
		addLineBreak();
	}

	private void unparseProperty(EReference property, boolean isConfigurationProperty)
	{
		String propertyName = property.getName();
		addText((isConfigurationProperty ? "configuration " : "") +  "property " + propertyName + ": ");
		unparseType(property.getEType());
		
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = QvtOperationalParserUtil.getInitExpression(property);
		if ( initExpression != null )
		{
			addText(" = ");
			unparseOCLExpression(initExpression);
		}
		
		addText(";");
		addLineBreak();
	}
	
	private void unparseIntermediateProperties(OperationalTransformation transformation)
	{
		EList<EStructuralFeature> intermediateProperties = transformation.getIntermediateProperty();
		
		for ( EStructuralFeature property : intermediateProperties )
		{
			unparseContextualProperty((ContextualProperty)property);
		}
		
		if ( intermediateProperties.size()> 0 )
			addLineBreak();
	}
	
	private void unparseModuleOperations(Module module)
	{
		for ( EOperation operation : module.getEOperations() )
		{
			if ( operation instanceof ImperativeOperation )
			{
				unparseImperativeOperation((ImperativeOperation)operation);
			}
			else
			{
				addText("/*___notAnImperativeOperation:" + operation.getName() + "___*/");
			}
		}
	}
	
	public Object visitImperativeOperation(ImperativeOperation operation)
	{
		unparseImperativeOperation(operation);
		return null;
	}

	private void unparseImperativeOperation(ImperativeOperation operation)
	{
		if ( operation instanceof Constructor )
		{
			unparseConstructor((Constructor)operation);
		}
		else if ( operation instanceof EntryOperation )
		{
			unparseEntryOperation((EntryOperation)operation);
		}
		else if ( operation instanceof MappingOperation )
		{
			unparseMappingOperation((MappingOperation)operation);
		}
		else if ( operation instanceof Helper )
		{
			unparseHelper((Helper)operation);
		}
		else
		{
			addText("/*___unknownTypeOfImperativeOperationFor:" + operation.getName() + "___*/");
		}
	}
	
	public Object visitConstructor(Constructor constructor)
	{
		unparseConstructor(constructor);
		return null;
	}

	private void unparseConstructor(Constructor constructor)
	{
		boolean isBlackbox = constructor.isIsBlackbox();
		if ( isBlackbox )
		{
			addText("blackbox ");
		}
		addText("constructor ");
		
		unparseHelperContext(constructor.getContext());

		addText(constructor.getName() + "(");
		unparseMappingParameters(constructor.getEParameters());
		addText(")");

		unparseVarParameters(constructor.getResult(),false);
		
		if ( isBlackbox )
		{
			addText(";");
			addLineBreak();
		}
		else
		{
			unparseConstructorBody((ConstructorBody)constructor.getBody());
		}
		addEmptyLine();
	}

	public Object visitConstructorBody(ConstructorBody constructorBody)
	{
		unparseConstructorBody(constructorBody);
		return null;
	}

	private void unparseConstructorBody(ConstructorBody constructorBody)
	{
		if ( constructorBody == null )
			addText("/*___OperationBodyWasNull___*/");
		else
		{
			indent();
			unparseOCLExpressionsLinebreakSeparatedInBraces(constructorBody.getContent(),true);
			unindent();
		}
	}
	
	public Object visitEntryOperation(EntryOperation entryOperation)
	{
		unparseEntryOperation(entryOperation);
		return null;
	}

	private void unparseEntryOperation(EntryOperation entryOperation)
	{
		if ( entryOperation.getResult() != null && entryOperation.getResult().size() > 0 )
		{
			addText("query ");
		}
		if ( entryOperation.getContext() != null )
		{
			unparseVarParameter(entryOperation.getContext());
		}
		addText(entryOperation.getName() + "(");
		unparseMappingParameters(entryOperation.getEParameters());
		addText(")");
		unparseVarParameters(entryOperation.getResult(),true);
		addLineBreak();
		unparseOperationBody(entryOperation.getBody());
		addLineBreak();
	}

	public Object visitOperationBody(OperationBody body)
	{
		unparseOperationBody(body);
		return null;
	}

	private void unparseOperationBody(OperationBody body)
	{
		if ( body != null )
		{
			String sectionName = "";
			unparseBodySection(sectionName, body.getContent());
		}
		else
		{
			addText("/*___OperationBodyWasNull___*/");
		}
	}

	public Object visitMappingOperation(MappingOperation operation)
	{
		unparseMappingOperation(operation);
		return null;
	}


	//
	// Would be better if QvtOperationalParserUtil.isAbstarctOperation were public
	//
	static boolean isAbstractOperation(ImperativeOperation operation) {
		EAnnotation annotation = operation.getEAnnotation(QvtOperationalParserUtil.QVT_NAMESPACE_URI);
		if(annotation != null) {
			String value = annotation.getDetails().get("abstract"/* QvtOperationalParserUtil.QVT_IS_ABSTRACT*/);
			return Boolean.valueOf(value);
		}
		return false;
	}
	
	
	private void unparseMappingOperation(MappingOperation operation)
	{
		boolean isBlackbox = operation.isIsBlackbox();
		if ( isBlackbox )
		{
			addText("blackbox ");
		}
		
		boolean isAbstract = isAbstractOperation(operation);
		if ( isAbstract )
		{
			addText("abstract ");
		}
		// p.75:
		// mapping inout <contexttype>::<mappingname> (<parameters>,) : <result-parameters>  ...
		
		VarParameter context = operation.getContext();
		String directionKindText = ( context != null && DirectionKind.INOUT.equals(context.getKind())) ? "inout " : ""; 
		
		addText("mapping " + directionKindText + getFullOperationName(operation) + "(");
		unparseMappingParameters(operation.getEParameters());
		addText(")");

		unparseMappingResultParameters(operation.getResult());
		addText(" ");

		unparseMappingExtensions(operation.getInherited(), operation.getMerged(), operation.getDisjunct());

		// <mapping_refinement> ::= 'refines' <scoped_identifier>
		if ( !isBlackbox )
		{
			unparseMappingRefinement(operation.getOverridden());
		}
		
		unparseMappingConditions(operation.getWhen(), operation.getWhere());
		
		if ( isBlackbox )
		{
			addText(";");
			addLineBreak();
		}
		else
		{
			boolean needsInit = checkMappingNeedsInit(operation);
			unparseMappingBody((MappingBody)operation.getBody(),needsInit);
			addEmptyLine();
		}
	}

	private String getFullOperationName(ImperativeOperation operation)
	{
		String contextString = constructContextString(operation.getContext());
		String fullName = contextString + operation.getName();
		return fullName;
	}

	private void unparseMappingResultParameters(EList<VarParameter> resultParameters)
	{
		if ( resultParameters == null || resultParameters.size() == 0 ) return;
		
		addText(" : ");
		
		boolean	withName = resultParameters.size() != 1;
		
		for (Iterator<VarParameter> iterator = resultParameters.iterator(); iterator.hasNext();)
		{
			VarParameter parameter = iterator.next();
			unparseMappingParameter(parameter,false,withName);
			if ( iterator.hasNext() )
			{
				addText(", ");
			}
		}
	}

	private void unparseMappingParameters(EList<EParameter> eParameters)
	{
		for (Iterator<EParameter> iterator = eParameters.iterator(); iterator.hasNext();)
		{
			EParameter parameter = iterator.next();
			unparseMappingParameter((VarParameter)parameter,true);
			if ( iterator.hasNext() )
			{
				addText(", ");
			}
		}
	}

	private void unparseMappingParameter(VarParameter parameter, boolean withDirectionAndName)
	{
		unparseMappingParameter(parameter,withDirectionAndName,withDirectionAndName);
	}
	
	private void unparseMappingParameter(VarParameter parameter, boolean withDirection, boolean withName)
	{
		String directionText = withDirection ? parameter.getKind().getLiteral() : "";
		String separatorText = withDirection && withName ? " " : "";
		String nameText = withName ? parameter.getName() : "";
		String semicolonText = withDirection || withName ? " : " : "";
		addText(directionText + separatorText + nameText + semicolonText);
		unparseType(parameter.getType());
		if ( parameter instanceof MappingParameter )
		{
			unparseExtentReference(((MappingParameter)parameter).getExtent());
		}
	}

	private void unparseMappingExtensions(EList<MappingOperation> inherited, EList<MappingOperation> merged, EList<MappingOperation> disjunct)
	{
		// <mapping_extension> ::= <mapping_extension_key> <scoped_identifier_list>
		// <mapping_extension_key> ::= 'inherits' | 'merges' | 'disjuncts'
		unparseMappingExtension("inherits",inherited);
		unparseMappingExtension("merges",merged);
		unparseMappingExtension("disjuncts",disjunct);
	}
	
	private void unparseMappingExtension(String relates, EList<MappingOperation> extendedOperations)
	{
		if ( extendedOperations != null && extendedOperations.size() > 0)
		{
			addLineBreak();
			addText(relates + " ");
			for (Iterator<MappingOperation> operationIterator = extendedOperations.iterator(); operationIterator.hasNext();)
			{
				addText(getFullOperationName(operationIterator.next()));
				if ( operationIterator.hasNext() )
				{
					addText(",");
				}
			}
		}
	}
	
	private void unparseMappingRefinement(ImperativeOperation refinedOperation)
	{
		if ( refinedOperation != null )
		{
			addLineBreak();
			addText("refines ");
			addText(getFullOperationName(refinedOperation));
			addLineBreak();
		}
	}

	private void unparseMappingConditions(EList<OCLExpression> when, OCLExpression where)
	{
		// when {<exprs>} where { <exprs>}
		unparseMappingConditionList("when",when);		
		unparseMappingCondition("where",guaranteeBooleanExp(where));
	}

	private void unparseMappingConditionList(String preOrPost, EList<OCLExpression> condition)
	{
		if ( condition != null && condition.size() > 0 )
		{
			addLineBreak();
			addText(preOrPost + " ");
			unparseOCLExpressions("{ ",condition,"; "," }",false,false,false);
		}
	}
	
	private void unparseMappingCondition(String preOrPost, OCLExpression oclExpression)
	{
		if ( oclExpression != null )
		{
			EList<OCLExpression> conditionList = new BasicEList<OCLExpression>();
			conditionList.add(oclExpression);
			unparseMappingConditionList(preOrPost,conditionList);
		}		
	}

	private OCLExpression guaranteeBooleanExp(OCLExpression expression)
	{
		if (expression instanceof BlockExp)
		{
			BlockExp block = (BlockExp) expression;
			EList<OCLExpression> body = block.getBody();
			return body.get(0);
		}
		else
		{
			return expression;
		}
	}
	
	private boolean checkMappingNeedsInit(MappingOperation operation)
	{
		EList<EParameter> mappingParameters = operation.getEParameters();
		EList<VarParameter> mappingResults = operation.getResult();
		EList<VarParameter> outParameters = new BasicEList<VarParameter>();
		
		for (Iterator<EParameter> iterator = mappingParameters.iterator(); iterator.hasNext();)
		{
			VarParameter mappingParameter = (VarParameter) iterator.next();
			DirectionKind directionKind = mappingParameter.getKind();
			if ( directionKind.equals(DirectionKind.INOUT) || directionKind.equals(DirectionKind.OUT) )
				outParameters.add(mappingParameter);
		}
		
		outParameters.addAll(mappingResults);
		
		for (Iterator<VarParameter> iterator = outParameters.iterator(); iterator.hasNext();)
		{
			VarParameter varParameter = iterator.next();
			EClassifier parameterType = varParameter.getType();
			if ( parameterType instanceof EClass &&  ((EClass)parameterType).isAbstract() ) return true;
		}
		
		return false;
	}

	public Object visitMappingBody(MappingBody mappingBody)
	{
		unparseMappingBody(mappingBody,false);
		return null;
	}

	private void unparseMappingBody(MappingBody mappingBody, boolean needsInit)
	{
		boolean hasInit = mappingBody.getInitSection() != null && mappingBody.getInitSection().size() > 0;
		boolean hasPopulation = mappingBody.getContent() != null && mappingBody.getContent().size() > 0;
		boolean hasEnd = mappingBody.getEndSection() != null && mappingBody.getEndSection().size() > 0;
		boolean hasBody = hasInit || hasPopulation || hasEnd;
		if ( hasBody )
		{
			addLineBreak();
			addTextAndIndent("{");
			addLineBreak();
			if ( hasInit || needsInit ) unparseBodySection("init",mappingBody.getInitSection());
			if ( hasPopulation ) unparsePopulationSection(mappingBody.getContent());
			if ( hasEnd ) unparseBodySection("end",mappingBody.getEndSection());
			unindentAndAddText("}");
		}
		else
		{
			addText(";");
		}
	}
	
	private void unparsePopulationSection(EList<OCLExpression> content)
	{
		if ( content == null || content.size() == 0 ) return;
		
		if ( content.size() == 1 && content.get(0) instanceof ObjectExp )
		{
			ObjectExp objExp = (ObjectExp) content.get(0);
			org.eclipse.ocl.ecore.Variable referredObject = objExp.getReferredObject();
			if ( referredObject != null )
			{
				if ( referredObject.getName() == "result" )
				{
					ConstructorBody constructorBody = objExp.getBody();
					if ( constructorBody == null )
						return;
					else
					{
						unparseOCLExpressionsLinebreakSeparated(constructorBody.getContent(),true);
						return;
					}
				};
			}
		}
		// default handling
		unparseBodySection("population", content);
	}
	
	public Object visitHelper(Helper helper)
	{
		unparseHelper(helper);
		return null;
	}

	private void unparseHelper(Helper helper)
	{
		boolean isBlackbox = helper.isIsBlackbox();
		
		if ( isBlackbox )
		{
			addText("blackbox ");
		}
		
		String keyword = helper.isIsQuery() ? "query " : "helper ";
		addText(keyword);

		unparseHelperContext(helper.getContext());
		addText(helper.getName() + "(");
		unparseMappingParameters(helper.getEParameters());
		addText(")");

		unparseVarParameters(helper.getResult(),true);
		
		if ( isBlackbox )
		{
			addText(";");
		}
		else
		{
			addLineBreak();
			unparseOperationBody(helper.getBody());
		}
		addLineBreak();
	}
	
	private void unparseBodySection(String sectionName,EList<OCLExpression> content)
	{
		if ( sectionName != null && sectionName.length() > 0 )
		{
			addText(sectionName);
			addLineBreak();
		}
		unparseOCLExpressionsLinebreakSeparatedInBraces(content,true);
		addLineBreak();
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>>
		void unparseOCLExpressions(String head, EList<T> content, String separatorSuffix, String tail, boolean suffixAlways, boolean indented, boolean onSeperateLines)
	{
		if ( head != null )
		{
			addText(head);
			if ( onSeperateLines )
				addLineBreak();
		}
		if ( indented )
			indent();
		for (Iterator<T> expressionIterator = content.iterator(); expressionIterator.hasNext();)
		{
			org.eclipse.ocl.expressions.OCLExpression<EClassifier> expression = expressionIterator.next();
			unparseOCLExpression(expression);
			if (expressionIterator.hasNext() || suffixAlways)
				addText(separatorSuffix);
			if ( onSeperateLines )
				addLineBreak();
		}
		if ( indented )
			unindent();
		if ( tail != null )
		{
			addText(tail);	
			if ( onSeperateLines )
				addLineBreak();
		}
	}

	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpressionsCommaSeparatedInBrackets(EList<T> list)
	{
		unparseOCLExpressions("[", list, ",", "]", false, false, false);
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpressionsCommaSeparatedInParentheses(EList<T> list)
	{
		unparseOCLExpressions("(", list, ",", ")", false, false, false);
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpressionsSemicolonSeparatedInBraces(EList<T> list, boolean suffixAlways)
	{
		unparseOCLExpressions("{", list, ";", " }", suffixAlways, false, false);
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpressionsLinebreakSeparated(EList<T> list, boolean suffixAlways)
	{
		unparseOCLExpressions(null, list, ";", null, suffixAlways, false, true);		
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpressionsLinebreakSeparatedInBraces(EList<T> list, boolean suffixAlways)
	{
		unparseOCLExpressions("{", list, ";", "}", suffixAlways, true, true);		
	}
	
	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseOCLExpression(T expression)
	{
		if ( expression == null ) return;
		
		if (expression instanceof CallExp)
		{
			unparseCallExpressions((CallExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.IfExp)
		{
			unparseIfExp((org.eclipse.ocl.ecore.IfExp)expression);
		}
		else if (expression instanceof ImperativeExpression)
		{
			unparseImperativeExpressions((ImperativeExpression)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.LetExp)
		{
			unparseLetExp((org.eclipse.ocl.ecore.LetExp)expression);
		}
		else if (expression instanceof LiteralExp)
		{
			unparseLiteralExpressions((org.eclipse.ocl.ecore.LiteralExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.MessageExp)
		{
			unparseMessageExp((org.eclipse.ocl.ecore.MessageExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.StateExp)
		{
			unparseStateExp((org.eclipse.ocl.ecore.StateExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.TypeExp)
		{
			unparseTypeExp((org.eclipse.ocl.ecore.TypeExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.UnspecifiedValueExp)
		{
			unparseUnspecifiedValueExp((org.eclipse.ocl.ecore.UnspecifiedValueExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.VariableExp)
		{
			unparseVariableExp((org.eclipse.ocl.ecore.VariableExp)expression);
		}
		else
		{
			addText("/*___unhandledOCLExpression:" + expression.getName() + "___*/");
		}
	}

	private void unparseCallExpressions(CallExp expression)
	{
		if (expression instanceof FeatureCallExp)
		{
			unparseFeatureCallExpressions((FeatureCallExp)expression);
		}
		else if (expression instanceof LoopExp)
		{
			unparseLoopExpressions((LoopExp)expression);
		}
		else if (expression instanceof ResolveExp)
		{
			unparseResolveExpressions((ResolveExp)expression);
		}
	}

	private void unparseFeatureCallExpressions(FeatureCallExp expression)
	{
		if (expression instanceof NavigationCallExp)
		{
			unparseNavigationCallExpressions((NavigationCallExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.OperationCallExp)
		{
			unparseOperationCallExpressions((org.eclipse.ocl.ecore.OperationCallExp)expression);
		}
	}

	private void unparseNavigationCallExpressions(NavigationCallExp expression)
	{
		if (expression instanceof org.eclipse.ocl.ecore.AssociationClassCallExp)
		{
			unparseAssociationClassCallExp((org.eclipse.ocl.ecore.AssociationClassCallExp)expression);
		}
		else if (expression instanceof OppositePropertyCallExp)
		{
			unparseOppositePropertyCallExp((OppositePropertyCallExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.PropertyCallExp)
		{
			unparsePropertyCallExp((org.eclipse.ocl.ecore.PropertyCallExp)expression);
		}
	}

	public Object visitAssociationClassCallExp(AssociationClassCallExp<EClassifier, EStructuralFeature> call)
	{
		unparseAssociationClassCallExp(call);
		return null;		
	}
	
	private void unparseAssociationClassCallExp(AssociationClassCallExp<EClassifier, EStructuralFeature> call)
	{
		unparseOCLExpression(call.getSource());
		addText("." + call.getNavigationSource().getName());
		unparseOCLExpressionsCommaSeparatedInBrackets(call.getQualifier());
		if ( call.isMarkedPre() )
		{
			addText("@pre");
		}
	}

	private void unparseOppositePropertyCallExp(OppositePropertyCallExp call)
	{
		unparseOCLExpression(call.getSource());
		addText("." + call.getReferredOppositeProperty().getName());
		unparseOCLExpressionsCommaSeparatedInBrackets(call.getQualifier());
		if ( call.isMarkedPre() )
		{
			addText("@pre");
		}
	}

	public Object visitPropertyCallExp(PropertyCallExp<EClassifier, EStructuralFeature> call)
	{
		unparsePropertyCallExp(call);
		return null;
	}

	private void unparsePropertyCallExp(PropertyCallExp<EClassifier, EStructuralFeature> call)
	{
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> source = call.getSource();
		if (
				( source instanceof VariableExp<?,?> ) &&
				( variableNameIsGeneratedTemp(((VariableExp<?,?>)source).getReferredVariable().getName()))
			)
		{
			addText("/* " + ((VariableExp<?,?>)source).getReferredVariable().getName() + "*/");
		}
		else
		{
			if ( source != null )
			{
				if ( source instanceof VariableExp<?,?> )
				{
					@SuppressWarnings("unchecked")
					boolean namePrinted = unparseVariableExp((VariableExp<EClassifier,EParameter>)source);
					if ( namePrinted )
					{
						addText(".");				
					}					
				}
				else
				{
					unparseOCLExpression(source);
					addText(".");				
				}
			}
			else
			{
				addText("/*___sourceIsNull___*/");				
			}
		}

		
		EStructuralFeature referredProperty = call.getReferredProperty();
		String propertyName = referredProperty.getName();
		String propertyText = escapeKeywordName(propertyName);
		addText(propertyText);

		EList<org.eclipse.ocl.expressions.OCLExpression<EClassifier>> qualifier = call.getQualifier();
		if ( qualifier != null && qualifier.size() > 0 )
			unparseOCLExpressionsCommaSeparatedInBrackets(qualifier);

		if ( call.isMarkedPre() )
		{
			addText("@pre");
		}
	}

	private void unparseOperationCallExpressions(org.eclipse.ocl.ecore.OperationCallExp expression)
	{
		if (expression instanceof ImperativeCallExp)
		{
			unparseImperativeCallExpressions((ImperativeCallExp)expression);
		}
		else if (expression instanceof LogExp)
		{
			unparseLogExp((LogExp)expression);
		}
		else
		{
			unparseOperationCallExp(expression);
		}
	}

	private void unparseImperativeCallExpressions(ImperativeCallExp expression)
	{
		if (expression instanceof MappingCallExp)
		{
			unparseMappingCallExp((MappingCallExp)expression);
		}
		else
		{
			unparseImperativeCallExp(expression);
		}
	}

	public Object visitMappingCallExp(MappingCallExp expression)
	{
		unparseMappingCallExp(expression);
		return null;
	}

	private void unparseMappingCallExp(MappingCallExp expression)
	{
		unparseOperationCallExp(expression);
	}

	private void unparseImperativeCallExp(ImperativeCallExp expression)
	{
		unparseOperationCallExp(expression);
	}

	public Object visitLogExp(LogExp log)
	{
		unparseLogExp(log);
		return null;
	}
	
	private void unparseLogExp(LogExp log)
	{
		addText("log");
		unparseOCLExpressionsCommaSeparatedInParentheses(log.getArgument());
		OCLExpression condition = log.getCondition();
		if ( condition != null)
		{
			addText(" when ");
			unparseOCLExpression(condition);
		}
	}
	
	public Object visitOperationCallExp(OperationCallExp<EClassifier, EOperation> expression)
	{
		unparseOperationCallExp(expression);
		return null;
	}

	private void unparseOperationCallExp(OperationCallExp<EClassifier, EOperation> expression)
	{
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> source = expression.getSource();
		
		String selector = "";
		boolean parenthesize = false;
		if ( source != null )
		{
			boolean sourceIsCollection = source.getType() instanceof CollectionType;
			selector = sourceIsCollection ? "->" : ".";
			
			if ( source.getType() instanceof Library )
				selector = "";
		}
		
		EOperation referredOperation = expression.getReferredOperation();
		String operationName = referredOperation != null ? referredOperation.getName() : "/*___referredOperationWasNull___*/";

		if ( expression instanceof MappingCallExp )
		{
			String mappingCallText = ((MappingCallExp)expression).isIsStrict() ? "xmap " : "map ";
			operationName = mappingCallText + operationName;
		}

		if ( operationName.equals("=") ||
				operationName.equals("+") ||
				operationName.equals("-") ||
				operationName.equals("*") ||
				operationName.equals("/") ||
				operationName.equals("%") ||
				operationName.equals(">") ||
				operationName.equals("<")
				)
		{
			selector = "";
			parenthesize = true;
		}
		else if ( operationName.equals("<>") || operationName.equals("!=") )
		{
			selector = "";
			operationName = " != ";
			parenthesize = true;
		}
		else if ( operationName.equals("and") )
		{
			selector = "";
			operationName = " and ";
			parenthesize = true;
		}		
		else if ( operationName.equals("or") )
		{
			selector = "";
			operationName = " or ";
			parenthesize = true;
		}
		
		if ( parenthesize ) addText("(");
		
		EList<org.eclipse.ocl.expressions.OCLExpression<EClassifier>> argumentList = expression.getArgument();
		boolean hasArguments = argumentList != null & argumentList.size() > 0;

		if ( operationName.equals("-") && !hasArguments )
		{
			//unary minus
			addText(selector + operationName);
			unparseOCLExpression((OCLExpression)source);
		}
		else
		{
			if ( source instanceof VariableExp )
			{
				@SuppressWarnings("unchecked")
				boolean isPrinted = unparseVariableExp((VariableExp<EClassifier, EParameter>)source);
				selector = isPrinted ? selector : "";
			}
			else
			{
				unparseOCLExpression((OCLExpression)source);
			}
			addText(selector + operationName);
			unparseArgumentList(argumentList);
		}
		if ( parenthesize ) addText(")");
	}

	private <T extends org.eclipse.ocl.expressions.OCLExpression<EClassifier>> void unparseArgumentList(EList<T> argumentList)
	{
		addText("(");
		for (Iterator<T> iterator = argumentList.iterator(); iterator.hasNext();)
		{
			org.eclipse.ocl.expressions.OCLExpression<EClassifier> argument = iterator.next();
			unparseOCLExpression((OCLExpression)argument);
			if ( iterator.hasNext() )
			{
				addText(", ");
			}
		}
		addText(")");
	}

	private void unparseLoopExpressions(LoopExp expression)
	{
		if (expression instanceof ImperativeLoopExp)
		{
			unparseImperativeLoopExpressions((ImperativeLoopExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.IterateExp)
		{
			unparseIterateExp((org.eclipse.ocl.ecore.IterateExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.IteratorExp)
		{
			unparseIteratorExp((org.eclipse.ocl.ecore.IteratorExp)expression);
		}
	}

	private void unparseImperativeLoopExpressions(ImperativeLoopExp expression)
	{
		if (expression instanceof ForExp)
		{
			unparseForExp((ForExp)expression);
		}
		else if (expression instanceof ImperativeIterateExp)
		{
			unparseImperativeIterateExp((ImperativeIterateExp)expression);
		}
	}

	public Object visitForExp(ForExp expression)
	{
		unparseForExp(expression);
		return null;
	}
	
	private void unparseForExp(ForExp expression)
	{
		// p 105
		// <source>-><for-name> (<iterator-list> | <condition>) <body> ;
		// <source>->< for-name> (<iterator-list> ) <body> ;
		unparseOCLExpression((OCLExpression)expression.getSource());

		addText("->" + expression.getName() + "(");
		
		unparseLoopExpIterator(expression.getIterator(),false);

		if ( expression.getCondition() != null )
		{
			addText("|");			
			unparseImperativeLoopExpCondition(expression.getCondition());			
		}
		addText(")");
		unparseBlockExpBody((OCLExpression)expression.getBody());
	}

	public Object visitImperativeIterateExp(ImperativeIterateExp expression)
	{
		unparseImperativeIterateExp(expression);
		return null;
	}

	private void unparseImperativeIterateExp(ImperativeIterateExp expression)
	{
		unparseOCLExpression((OCLExpression)expression.getSource());
		
		addText("->" + expression.getName() + "(");

		unparseLoopExpIterator(expression.getIterator(),true);
		
		if ( expression.getTarget() != null )
		{
			// <source> -> <collector-name> (<iterator-list>; <target> = <body> | <condition>) ;
			addText(";");
			unparseVariable(expression.getTarget(),false);
			addText("=");
			unparseOCLExpression((OCLExpression)expression.getBody());
			addText("|");
			unparseImperativeLoopExpCondition(expression.getCondition());
		}
		else
		{
			// <source> -> <collector-name> (<iterator-list> | <body_or_condition>) ;
			// <source> -> <collector-name> (<body_or_condition>) ;
			addText("|");
			unparseImperativeLoopExpBodyXorCondition(expression);			
		}
		addText(")");
	}

	private void unparseLoopExpIterator(EList<Variable<EClassifier, EParameter>> loopIteratorList, boolean withType)
	{
		if ( loopIteratorList != null && loopIteratorList.size() > 0 )
		{
			for (Iterator<Variable<EClassifier, EParameter>> iterator = loopIteratorList.iterator(); iterator.hasNext();)
			{
				Variable<EClassifier, EParameter> loopIterator =
						(Variable<EClassifier, EParameter>) iterator.next();
				unparseVariableNameType(loopIterator,withType);
				if ( iterator.hasNext() )
					addText(",");
			}
		}
	}

	private void unparseImperativeLoopExpBodyXorCondition(ImperativeLoopExp expression)
	{
		if ( expression.getBody() != null && expression.getCondition() == null )
		{
			unparseLoopExpBody((OCLExpression)expression.getBody());
		}
		else if ( expression.getBody() == null && expression.getCondition() != null )
		{
			unparseImperativeLoopExpCondition(expression.getCondition());			
		}
		else
		{
			addText("/*body:"+expression.getBody() + "; condition:" + expression.getCondition() + "*/");
		}
	}

	private void unparseLoopExpBody(OCLExpression body)
	{
		unparseOCLExpression(body);
	}
	
	private void unparseImperativeLoopExpCondition(OCLExpression condition)
	{
		if ( condition instanceof org.eclipse.ocl.ecore.TypeExp )
		{
			unparseTypeExp((org.eclipse.ocl.ecore.TypeExp) condition);
		}
		else
		{
			unparseOCLExpression(condition);
		}
	}

	public Object visitIterateExp(IterateExp<EClassifier, EParameter> iterateExp)
	{
		unparseIterateExp(iterateExp);
		return null;
	}

	private void unparseIterateExp(IterateExp<EClassifier, EParameter> iterateExp)
	{
//		<iterate_exp> ::= 'iterate' '(' <declarator_list> ';' <declarator> '|' <expression> ')'
		
		unparseOCLExpression((OCLExpression)iterateExp.getSource());
		addText("->" + "iterate" + "(");
		unparseVariablesCommaSeparated(iterateExp.getIterator());
		addText(";");
		unparseVariable(iterateExp.getResult(),true);
		addText("|");
		unparseOCLExpression(iterateExp.getBody());
		addText(")");
	}

	public Object visitIteratorExp(IteratorExp<EClassifier, EParameter> expression)
	{
		unparseIteratorExp(expression);
		return null;
	}

	private void unparseIteratorExp(IteratorExp<EClassifier, EParameter> expression)
	{
		unparseOCLExpression((OCLExpression)expression.getSource());
		addText("->" + expression.getName() + "(");
		unparseLoopExpIterator(expression.getIterator(),true);
		addText("|");
		unparseLoopExpBody((OCLExpression)expression.getBody());
		addText(")");
	}

	private void unparseResolveExpressions(ResolveExp expression)
	{
		if (expression instanceof ResolveInExp)
		{
			unparseResolveInExp((ResolveInExp)expression);
		}
		else
		{
			unparseResolveExp((ResolveExp)expression);
		}		
	}

	public Object visitResolveExp(ResolveExp expression)
	{
		unparseResolveExp(expression);
		return null;
	}

	public Object visitResolveInExp(ResolveInExp expression)
	{
		unparseResolveInExp(expression);
		return null;
	}

	private void unparseResolveExp(ResolveExp expression)
	{
		// <resolve_exp> ::= <resolve_key> '(' <resolve_condition>? ')'
		// <resolve_key> ::= 'late'? <resolve_kind>
		// <resolve_kind> ::= 'resolve' | 'resolveone' | 'invresolve' | 'invresolveone'
		// <resolve_condition> ::= <declarator> ('|' <expression>)?
		unparseSource(expression.getSource());
				
		String resolveKindText = constructResolveKindText(expression);
		addText(resolveKindText +"(");
		
		org.eclipse.ocl.ecore.Variable target = expression.getTarget();
		if ( target != null )
		{
			boolean hasCondition = expression.getCondition() != null;
			String targetNameText = hasCondition ? target.getName() + " : " : "";
			addText(targetNameText);
			unparseType(target.getType());
		}

		unparseResolveExpCondition(expression.getCondition());

		addText(")");
	}

	private void unparseResolveInExp(ResolveInExp expression)
	{
		// <resolve_in_exp> ::= <resolve_in_key> '(' <scoped_identifier> (',' <resolve_condition>)?')'
		// <resolve_in_key> ::= 'late'? <resolve_in_kind>
		// <resolve_in_kind> ::= 'resolveIn' | 'resolveoneIn' | 'invresolveIn' | 'invresolveoneIn'
		// <resolve_condition> ::= <declarator> ('|' <expression>)?
		unparseSource(expression.getSource());
		String resolveInKindText = constructResolveKindText(expression);
		resolveInKindText += "In";
		addText(resolveInKindText +"(");

		MappingOperation inMapping = expression.getInMapping();
		String inMappingText = inMapping != null ? getFullOperationName(inMapping) : "/*___InMappingWasNull___*/";
		addText(inMappingText);

		org.eclipse.ocl.ecore.Variable target = expression.getTarget();
		if ( target != null )
		{
			boolean hasCondition = expression.getCondition() != null;
			String targetNameText = hasCondition ? target.getName() + " : " : "";
			addText(", " + targetNameText);
			unparseType(target.getType());
		}

		unparseResolveExpCondition(expression.getCondition());

		addText(")");
	}

	private void unparseSource(org.eclipse.ocl.expressions.OCLExpression<EClassifier> source)
	{
		if ( source != null )
		{
			if ( source instanceof VariableExp )
			{
				@SuppressWarnings("unchecked")
				boolean isPrinted = unparseVariableExp((VariableExp<EClassifier, EParameter>)source);
				addText(isPrinted ? "." : "");
			}
			else
			{
				unparseOCLExpression((OCLExpression)source);
				addText(".");
			}
		}
	}

	private String constructResolveKindText(ResolveExp expression)
	{
		String resolveInKindText = "resolve";
		resolveInKindText = expression.isIsInverse() ? "inv" + resolveInKindText : resolveInKindText;
		resolveInKindText += expression.isOne() ? "one" : "";
		resolveInKindText = expression.isIsDeferred() ? ("late " + resolveInKindText) : resolveInKindText;
		return resolveInKindText;
	}

	private void unparseResolveExpCondition(OCLExpression resolveCondition)
	{
		if ( resolveCondition != null )
		{
			addText(" | ");
			unparseImperativeLoopExpCondition(resolveCondition);
		}
	}

	public Object visitIfExp(IfExp<EClassifier> expression)
	{
		unparseIfExp(expression);
		return null;
	}
	
	private void unparseIfExp(IfExp<EClassifier> expression)
	{
		// <if_exp> ::= 'if' <expression> <then_part>
		//				<elif_part>* <else_part>? 'endif'
		// <then_part> ::= 'then' <if_body>
		// <elif_part> ::= 'elif' <if_body>
		// <else_part> ::= 'else' <if_body>
		// <if_body> ::= <expression> | <expression_block>
		addText("if (");
		unparseOCLExpression((OCLExpression)expression.getCondition());
		addText(")");
		indent();
		addLineBreak();
		addTextAndIndent("then ");
		unparseOCLExpression((OCLExpression)expression.getThenExpression());
		if ( expression.getElseExpression() != null )
		{
			addLineBreak();
			unindentAndAddText("else ");
			indent();
			unparseOCLExpression((OCLExpression)expression.getElseExpression());
		}
		addLineBreak();
		unindentAndAddText("endif ");
		unindent();
	}

	private void unparseImperativeExpressions(ImperativeExpression expression)
	{
		if (expression instanceof AltExp)
		{
			unparseAltExp((AltExp) expression);
		}
		else if (expression instanceof AssertExp)
		{
			unparseAssertExp((AssertExp) expression);
		}
		else if (expression instanceof AssignExp)
		{
			unparseAssignExp((AssignExp) expression);
		}
		else if (expression instanceof BlockExp)
		{
			unparseBlockExp((BlockExp) expression, true, false);
		}
		else if (expression instanceof BreakExp)
		{
			unparseBreakExp((BreakExp) expression);
		}
		else if (expression instanceof CatchExp)
		{
			unparseCatchExp((CatchExp) expression);
		}
		else if (expression instanceof ComputeExp)
		{
			unparseComputeExp((ComputeExp) expression);
		}
		else if (expression instanceof ContinueExp)
		{
			unparseContinueExp((ContinueExp) expression);
		}
		else if (expression instanceof InstantiationExp)
		{
			unparseInstantiationExpressions((InstantiationExp) expression);
		}
		else if (expression instanceof RaiseExp)
		{
			unparseRaiseExp((RaiseExp) expression);
		}
		else if (expression instanceof ReturnExp)
		{
			unparseReturnExp((ReturnExp) expression);
		}
		else if (expression instanceof SwitchExp)
		{
			unparseSwitchExp((SwitchExp) expression);
		}
		else if (expression instanceof TryExp)
		{
			unparseTryExp((TryExp) expression);
		}
		else if (expression instanceof UnlinkExp)
		{
			unparseUnlinkExp((UnlinkExp) expression);
		}
		else if (expression instanceof UnpackExp)
		{
			unparseUnpackExp((UnpackExp) expression);
		}
		else if (expression instanceof VariableInitExp)
		{
			unparseVariableInitExp((VariableInitExp) expression);
		}
		else if (expression instanceof WhileExp)
		{
			unparseWhileExp((WhileExp) expression);
		}		
	}

	public Object visitAltExp(AltExp astNode)
	{
		unparseAltExp(astNode);
		return null;
	}

	private void unparseAltExp(AltExp astNode)
	{
		addText("AltExp");
	}

	public Object visitAssertExp(AssertExp assertExpression)
	{
		unparseAssertExp(assertExpression);
		return null;
	}
	
	private void unparseAssertExp(AssertExp assertExpression)
	{
		addText("assert ");
		
		SeverityKind severity =	assertExpression.getSeverity();
		if ( severity != null )
			addText(severity.getLiteral() + " ");
		
		addText("(");
		unparseOCLExpression(assertExpression.getAssertion());
		addText(")");
		
		LogExp logExpression = assertExpression.getLog();
		if ( logExpression != null )
		{
			addText(" with ");
			unparseLogExp(logExpression);
		}
	}

	public Object visitAssignExp(AssignExp assignment)
	{
		unparseAssignExp(assignment);
		return null;
	}
	
	private void unparseAssignExp(AssignExp assignment)
	{
		unparseOCLExpression(assignment.getLeft());
		
		String operatorText = assignment.isIsReset() ? " := " : " += ";
		addText(operatorText);

		if ( assignment.getValue().size() == 1 )
		{
			unparseOCLExpression(assignment.getValue().get(0));
		}
		else
		{
			unparseOCLExpressionsSemicolonSeparatedInBraces(assignment.getValue(), false);
		}
	}

	public Object visitBlockExp(BlockExp block)
	{
		unparseBlockExp(block,true,false);
		return null;
	}

	private void unparseBlockExp(BlockExp block, boolean printDo, boolean suffixAlways)
	{
		if ( printDo )
			addText("do ");
		unparseOCLExpressionsSemicolonSeparatedInBraces(block.getBody(),suffixAlways);
	}

	public Object visitBreakExp(BreakExp expression)
	{
		unparseBreakExp(expression);
		return null;
	}

	private void unparseBreakExp(BreakExp expression)
	{
		addText("break");
	}

	public Object visitCatchtExp(CatchExp expression)
	{
		unparseCatchExp(expression);
		return null;
	}
	
	private void unparseCatchExpressions(EList<CatchExp> catchExpressions)
	{
		for (Iterator<CatchExp> iterator = catchExpressions.iterator(); iterator.hasNext();)
		{
			CatchExp catchExpression = iterator.next();
			unparseCatchExp(catchExpression);
		}
	}

	private void unparseCatchExp(CatchExp expression)
	{
		addText("except (");

		EAnnotation catchVarAnnotation = expression.getEAnnotation(QvtOperationalParserUtil.QVT_NAMESPACE_URI + "/catchVar");
		if ( catchVarAnnotation != null )
		{
			unparseCatchVarAnnotation(catchVarAnnotation);
		}

		unparseCatchExpExceptions(expression.getException());
		
		addText(") ");
		unparseTryBody(expression.getBody());
	}

	private void unparseCatchVarAnnotation(EAnnotation catchVarAnnotation)
	{
		EList<EObject> contents = catchVarAnnotation.getContents();
		
		if ( contents != null && contents.size() > 0 )
		{
			EObject exceptionObject = contents.get(0);
			
			if ( exceptionObject instanceof Variable<?,?> )
			{
				@SuppressWarnings("unchecked")
				Variable<EClassifier,EObject> catchVariable = (Variable<EClassifier,EObject>) exceptionObject;
				addText(catchVariable.getName() + ":");
			}
		}
	}

	private void unparseCatchExpExceptions(EList<EClassifier> exceptions)
	{
		if ( exceptions == null ) return;
		
		for (Iterator<EClassifier> iterator = exceptions.iterator(); iterator.hasNext();)
		{
			EClassifier exception = iterator.next();
			
			addText(exception.getName());
			if ( iterator.hasNext() )
			{
				addText(",");
			}
		}
	}

	public Object visitComputeExp(ComputeExp expression)
	{
		unparseComputeExp(expression);
		return null;
	}

	private void unparseComputeExp(ComputeExp expression)
	{
		org.eclipse.ocl.ecore.Variable variable = expression.getReturnedElement();
		OCLExpression body = expression.getBody();

		if ( body instanceof WhileExp )
		{
			WhileExp whileExp = (WhileExp) body;
			addText("while (");
			unparseVariable(variable,true);
			addText(";");
			unparseOCLExpression(whileExp.getCondition());
			addText(")");	
			unparseBlockExpBody(whileExp.getBody());
		}
		else
		{
			addText("compute (");
			unparseVariable(variable,true);
			addText(")");	
			unparseBlockExpBody(body);
		}		
	}

	public Object visitContinueExp(ContinueExp expression)
	{
		unparseContinueExp(expression);
		return null;
	}

	private void unparseContinueExp(ContinueExp expression)
	{
		addText("continue");
	}

	private void unparseInstantiationExpressions(InstantiationExp expression)
	{
		if (expression instanceof ObjectExp)
		{
			unparseObjectExp((ObjectExp)expression);
		}
		else
		{
			unparseInstantiationExp(expression);
		}
	}

	public Object visitObjectExp(ObjectExp expression)
	{
		unparseObjectExp(expression);
		return null;
	}
	
	private void unparseObjectExp(ObjectExp expression)
	{
		addText("object ");
		if ( expression.getReferredObject() != null )
		{
			unparseVariableNameType(expression.getReferredObject());
		}
		unparseExtentReference(expression.getExtent());	
		addText(" ");
		unparseConstructorBody(expression.getBody());
	}

	private void unparseExtentReference(org.eclipse.ocl.ecore.Variable extent)
	{
		if ( extent != null )
		{
			if ( ! extent.getName().startsWith(QvtOperationalEnv.GENERATED_NAME_SPECIAL_PREFIX) )
			{
				addText("@" + extent.getName());
			}
		}
	}

	public Object visitInstantiationExp(InstantiationExp expression)
	{
		unparseInstantiationExp(expression);
		return null;
	}
	
	private void unparseInstantiationExp(InstantiationExp expression)
	{
		addText("new ");
		unparseType(expression.getType());
		unparseExtentReference(expression.getExtent());
		unparseArgumentList(expression.getArgument());
	}

	public Object visitRaiseExp(RaiseExp raise)
	{
		unparseRaiseExp(raise);
		return null;
	}
	
	private void unparseRaiseExp(RaiseExp raise)
	{
		// <raise_exp> ::= 'raise' <scoped_identifier> ('(' <arg_list>? ')')?
		// <arg_list> ::= <expression_comma_list>
		raise.getArgument();
		raise.getException();
		addText("raise ");
		unparseType(raise.getException());
		if ( raise.getArgument() != null )
		{
			addText("(");
			unparseOCLExpression(raise.getArgument());
			addText(")");
		}
	}

	public Object visitReturnExp(ReturnExp expression)
	{
		unparseReturnExp(expression);
		return null;
	}

	private void unparseReturnExp(ReturnExp expression)
	{
		//<return_exp> ::= 'return' <expression>?
		
		addText("return");
		if ( expression.getValue() != null )
		{
			addText(" ");
			unparseOCLExpression(expression.getValue());
		}
	}

	public Object visitSwitchExp(SwitchExp svvitch)
	{
		unparseSwitchExp(svvitch);
		return null;
	}

	private void unparseSwitchExp(SwitchExp svvitch)
	{
		// <switch_exp> ::= 'switch' ('(' <iter_declarator> ')')? <switch_body>
		// <switch_body> ::= '{' <switch_alt>+ <switch_else>? '}'
		// <switch_alt> ::= 'case' '(' <expression> ')' <expression_statement>
		// <switch_else> ::= 'else' <expression_statement>
		svvitch.getElsePart();

		addTextAndIndent("switch {");
		addLineBreak();
		EList<AltExp> altExpList = svvitch.getAlternativePart();
		for (AltExp altExp : altExpList)
		{
			addText("case (");
			unparseOCLExpression(altExp.getCondition());
			addText(") ");
			unparseOCLExpression(altExp.getBody());
			addText(";");
			addLineBreak();
		}
		if ( svvitch.getElsePart() != null )
		{
			addText("else ");
			unparseOCLExpression(svvitch.getElsePart());
			addText(";");
			addLineBreak();
		}
		unindentAndAddText("}");
	}

	public Object visitTryExp(TryExp expression)
	{
		unparseTryExp(expression);
		return null;
	}

	private void unparseTryExp(TryExp expression)
	{
		addText("try ");
		EList<OCLExpression> tryBody = expression.getTryBody();
		unparseTryBody(tryBody);
		unparseCatchExpressions(expression.getExceptClause());
	}

	private void unparseTryBody(EList<OCLExpression> tryBody)
	{
		EList<OCLExpression> unparseBody = tryBody;
		
		if ( unparseBody.size() == 1 &&  unparseBody.get(0) instanceof BlockExp )
		{
			BlockExp block = (BlockExp) unparseBody.get(0);
			unparseBody = block.getBody();
		}
		
		unparseOCLExpressionsLinebreakSeparatedInBraces(unparseBody,true);
		addLineBreak();
	}

	public Object visitUnlinkExp(UnlinkExp unlink)
	{
		unparseUnlinkExp(unlink);
		return null;
	}

	private void unparseUnlinkExp(UnlinkExp unlink)
	{
		unparseOCLExpression(unlink.getTarget());
		addText(".unlink(");
		unparseOCLExpression(unlink.getItem());
		addText(")");
	}

	public Object visitUnpackExp(UnpackExp unpack)
	{
		unparseUnpackExp(unpack);
		return null;
	}

	private void unparseUnpackExp(UnpackExp unpack)
	{
		addText("var ");
		unparseOCLVariablesCommaSeparated(unpack.getTargetVariable());
		addText(" := ");
		unparseOCLExpression(unpack.getSource());
	}

	private void unparseOCLVariablesCommaSeparated(EList<org.eclipse.ocl.ecore.Variable> list)
	{
		unparseOCLVariables("","",list,",","");
	}

	private void unparseOCLVariables(String head, String prefix, EList<org.eclipse.ocl.ecore.Variable> list, String separatorSuffix, String tail)
	{
		addText(head);
		for (Iterator<org.eclipse.ocl.ecore.Variable> iterator = list.iterator(); iterator.hasNext();)
		{
			org.eclipse.ocl.ecore.Variable variable = iterator.next();
			addText(prefix);
			unparseVariable(variable,false);
			if (iterator.hasNext())
				addText(separatorSuffix);
		}
		addText(tail);
	}

	private void unparseVariablesCommaSeparated(EList<Variable<EClassifier, EParameter>> list)
	{
		unparseVariables("","",list,",","");
	}

	private void unparseVariables(String head, String prefix, EList<Variable<EClassifier, EParameter>> list, String separatorSuffix, String tail)
	{
		addText(head);
		for (Iterator<Variable<EClassifier, EParameter>> iterator = list.iterator(); iterator.hasNext();)
		{
			Variable<EClassifier, EParameter> variable = iterator.next();
			addText(prefix);
			unparseVariable(variable,false);
			if (iterator.hasNext())
				addText(separatorSuffix);
		}
		addText(tail);
	}


	public Object visitVariableInitExp(VariableInitExp variableInit)
	{
		unparseVariableInitExp(variableInit);
		return null;
	}

	private void unparseVariableInitExp(VariableInitExp variableInit)
	{
		org.eclipse.ocl.ecore.Variable variable = variableInit.getReferredVariable();
		String assignmentOp = variableInit.isWithResult() ? " ::= " : " := ";
		
		addText("var ");
		unparseVariable(variable,false);
		OCLExpression initExpression = (OCLExpression)variable.getInitExpression();
		if ( initExpression != null )
		{
			addText(assignmentOp);
			unparseOCLExpression(initExpression);
		}
	}

	public Object visitWhileExp(WhileExp vvhile)
	{
		unparseWhileExp(vvhile);
		return null;
	}

	private void unparseWhileExp(WhileExp vvhile)
	{
		addText("while ( ");
		unparseOCLExpression(vvhile.getCondition());
		addText(" )");		
		
		unparseBlockExpBody(vvhile.getBody());
	}

	private void unparseBlockExpBody(OCLExpression body)
	{
		if ( body instanceof BlockExp )
		{
			BlockExp block = (BlockExp) body;
			unparseBlockExp(block,false,false);
		}
		else
		{
			addText("{ ");
			unparseOCLExpression(body);
			addText(" }");
		}
	}

	public Object visitLetExp(LetExp<EClassifier, EParameter> letExp)
	{
		unparseLetExp(letExp);
		return null;
	}

	private void unparseLetExp(LetExp<EClassifier, EParameter> letExp)
	{
		// <let_exp> ::= 'let' <declarator_list> 'in' <expression>
		addText("let ");
		unparseVariable(letExp.getVariable(),true);
		addText(" in ");
		unparseOCLExpression(letExp.getIn());
	}

	private void unparseLiteralExpressions(LiteralExp expression)
	{
		if (expression instanceof org.eclipse.ocl.ecore.CollectionLiteralExp)
		{
			unparseCollectionLiteralExp((org.eclipse.ocl.ecore.CollectionLiteralExp) expression);
		}
		else if (expression instanceof DictLiteralExp)
		{
			unparseDictLiteralExp((DictLiteralExp) expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.EnumLiteralExp)
		{
			unparseEnumLiteralExp((org.eclipse.ocl.ecore.EnumLiteralExp) expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.InvalidLiteralExp)
		{
			unparseInvalidLiteralExp((org.eclipse.ocl.ecore.InvalidLiteralExp) expression);
		}
		else if (expression instanceof ListLiteralExp)
		{
			unparseListLiteralExp((ListLiteralExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.NullLiteralExp)
		{
			unparseNullLiteralExp((org.eclipse.ocl.ecore.NullLiteralExp) expression);
		}
		else if (expression instanceof OrderedTupleLiteralExp)
		{
			unparseOrderedTupleLiteralExp((OrderedTupleLiteralExp) expression);
		}
		else if (expression instanceof PrimitiveLiteralExp)
		{
			unparsePrimitiveLiteralExpressions((PrimitiveLiteralExp)expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.TupleLiteralExp)
		{
			unparseTupleLiteralExp((org.eclipse.ocl.ecore.TupleLiteralExp) expression);
		}
	}
	
	public Object visitCollectionLiteralExp(CollectionLiteralExp<EClassifier> literalExp)
	{
		unparseCollectionLiteralExp(literalExp);
		return null;
	}

	private void unparseCollectionLiteralExp(CollectionLiteralExp<EClassifier> literalExp)
	{
		if (literalExp.getKind().equals(CollectionKind.SEQUENCE_LITERAL) && literalExp.getType() instanceof ListType )
		{
			unparseCollectionLiteralAsListLiteral(literalExp);
			return;
		}
		String collectionTypeText = literalExp.getKind().getName();
		EList<CollectionLiteralPart<EClassifier>> partList = literalExp.getPart();
		unparseCollectionLiteralExp_TypeAndParts(collectionTypeText, partList);
	}

	private void unparseCollectionLiteralExp_TypeAndParts(String collectionTypeText,EList<CollectionLiteralPart<EClassifier>> partList)
	{
		addText(collectionTypeText + " {");
		unparseCollectionLiteralParts(partList);
		addText("}");
	}
	
	private void unparseCollectionLiteralParts(EList<CollectionLiteralPart<EClassifier>> collectionLiteralPartList)
	{
		for (Iterator<CollectionLiteralPart<EClassifier>> partItor = collectionLiteralPartList.iterator(); partItor.hasNext();)
		{
			unparseCollectionLiteralPart(partItor.next());
			if ( partItor.hasNext() ) addText(",");
		}
	}

	private void unparseCollectionLiteralPart(CollectionLiteralPart<EClassifier> collectionLiteralPart)
	{
		if ( collectionLiteralPart instanceof CollectionItem )
		{
			unparseCollectionItem((CollectionItem<EClassifier>) collectionLiteralPart);
		}
		else if ( collectionLiteralPart instanceof CollectionRange )
		{
			unparseCollectionRange((CollectionRange<EClassifier>) collectionLiteralPart);
		}
		else
		{
			addText("/*CollectionLiteralPart*/");
		}
	}
	
	public Object visitDictLiteralExp(DictLiteralExp dictLiteral)
	{
		unparseDictLiteralExp(dictLiteral);
		return null;
	}

	private void unparseDictLiteralExp(DictLiteralExp dictLiteral)
	{
		//		<literal_dict> ::= 'Dict' '{' <dict_item_list>? '}'
		//		<dict_item_list> ::= <dict_item> (',' <dict_item>)*
		//		<dict_item> ::= <literal_simple> '=' <expression>
		addText("Dict {");
		unparseDictLiteralParts(dictLiteral.getPart());
		addText("}");
	}

	private void unparseDictLiteralParts(EList<DictLiteralPart> dictLiteralPartList)
	{
		for (Iterator<DictLiteralPart> partItor = dictLiteralPartList.iterator(); partItor.hasNext();)
		{
			unparseDictLiteralPart(partItor.next());
			if ( partItor.hasNext() ) addText(",");
		}
	}

	public Object visitDictLiteralPart(DictLiteralPart dictLiteralPart)
	{
		unparseDictLiteralPart(dictLiteralPart);
		return null;
	}

	private void unparseDictLiteralPart(DictLiteralPart dictLiteralPart)
	{
		unparseOCLExpression(dictLiteralPart.getKey());
		addText(" = ");
		unparseOCLExpression(dictLiteralPart.getValue());
	}

	public Object visitEnumLiteralExp(EnumLiteralExp<EClassifier, EEnumLiteral> enumLiteral)
	{
		unparseEnumLiteralExp(enumLiteral);
		return null;
	}

	private void unparseEnumLiteralExp(EnumLiteralExp<EClassifier, EEnumLiteral> enumLiteral)
	{
		EEnumLiteral referredEnumLiteral = enumLiteral.getReferredEnumLiteral();
		EClassifier enumType = enumLiteral.getType();
		unparseType(enumType);

		String enumName = enumLiteral.getName();			

		if ( referredEnumLiteral != null )
		{
			enumName = referredEnumLiteral.getLiteral();		
		}
		
		enumName = escapeKeywordName(enumName);

		addText("::" + enumName);
	}

	public Object visitInvalidLiteralExp(InvalidLiteralExp<EClassifier> invalidLiteral)
	{
		unparseInvalidLiteralExp(invalidLiteral);
		return null;
	}

	private void unparseInvalidLiteralExp(InvalidLiteralExp<EClassifier> invalidLiteral)
	{
		addText("invalid/*___InvalidLiteralExp___*/");
	}

	private void unparseListLiteralExp(ListLiteralExp listLiteral)
	{
		addText("List ");
		unparseOCLExpressionsSemicolonSeparatedInBraces(listLiteral.getElement(), false);
	}

	private void unparseCollectionLiteralAsListLiteral(CollectionLiteralExp<EClassifier> collectionLiteral)
	{
		unparseCollectionLiteralExp_TypeAndParts("List", collectionLiteral.getPart());
	}

	public Object visitNullLiteralExp(NullLiteralExp<EClassifier> literalExp)
	{
		unparseNullLiteralExp(literalExp);
		return null;
	}

	private void unparseNullLiteralExp(NullLiteralExp<EClassifier> literalExp)
	{
		addText("null");
	}

	public Object visitOrderedTupleLiteralExp(OrderedTupleLiteralExp tuple)
	{
		unparseOrderedTupleLiteralExp(tuple);
		return null;
	}

	private void unparseOrderedTupleLiteralExp(OrderedTupleLiteralExp tuple)
	{
		addText("Tuple {");
		unparseOrderedTupleLiteralParts(tuple.getPart());
		addText("}");
	}

	private void unparseOrderedTupleLiteralParts(EList<OrderedTupleLiteralPart> partList)
	{
		for (Iterator<OrderedTupleLiteralPart> partItor = partList.iterator(); partItor.hasNext();)
		{
			unparseOrderedTupleLiteralPart(partItor.next());
			if ( partItor.hasNext() ) addText(",");
		}
	}

	public Object visitOrderedTupleLiteralPart(OrderedTupleLiteralPart part)
	{
		unparseOrderedTupleLiteralPart(part);
		return null;
	}

	private void unparseOrderedTupleLiteralPart(OrderedTupleLiteralPart part)
	{
		unparseOCLExpression(part.getValue());
	}

	private void unparsePrimitiveLiteralExpressions(PrimitiveLiteralExp expression)
	{
		if (expression instanceof org.eclipse.ocl.ecore.BooleanLiteralExp)
		{
			unparseBooleanLiteralExp((org.eclipse.ocl.ecore.BooleanLiteralExp) expression);
		}
		else if (expression instanceof NumericLiteralExp)
		{
			unparseNumericLiteralExp((NumericLiteralExp) expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.StringLiteralExp)
		{
			unparseStringLiteralExp((org.eclipse.ocl.ecore.StringLiteralExp) expression);
		}
	}

	public Object visitBooleanLiteralExp(BooleanLiteralExp<EClassifier> literal)
	{
		unparseBooleanLiteralExp(literal);
		return null;
	}

	private void unparseBooleanLiteralExp(BooleanLiteralExp<EClassifier> literal)
	{
		addText(literal.getBooleanSymbol().toString());
	}

	private void unparseNumericLiteralExp(NumericLiteralExp expression)
	{
		if (expression instanceof org.eclipse.ocl.ecore.IntegerLiteralExp)
		{
			unparseIntegerLiteralExp((org.eclipse.ocl.ecore.IntegerLiteralExp) expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.RealLiteralExp)
		{
			unparseRealLiteralExp((org.eclipse.ocl.ecore.RealLiteralExp) expression);
		}
		else if (expression instanceof org.eclipse.ocl.ecore.UnlimitedNaturalLiteralExp)
		{
			unparseUnlimitedNaturalLiteralExp((org.eclipse.ocl.ecore.UnlimitedNaturalLiteralExp) expression);
		}
	}
	
	public Object visitIntegerLiteralExp(IntegerLiteralExp<EClassifier> literal)
	{
		unparseIntegerLiteralExp(literal);
		return null;
	}

	private void unparseIntegerLiteralExp(IntegerLiteralExp<EClassifier> literal)
	{
		addText(literal.getIntegerSymbol().toString());
	}

	public Object visitRealLiteralExp(RealLiteralExp<EClassifier> literal)
	{
		unparseRealLiteralExp(literal);
		return null;
	}

	private void unparseRealLiteralExp(RealLiteralExp<EClassifier> literal)
	{
		addText(literal.getRealSymbol().toString());
	}

	public Object visitUnlimitedNaturalLiteralExp(UnlimitedNaturalLiteralExp<EClassifier> literal)
	{
		unparseUnlimitedNaturalLiteralExp(literal);
		return null;
	}

	private void unparseUnlimitedNaturalLiteralExp(UnlimitedNaturalLiteralExp<EClassifier> literal)
	{
		addText(literal.toString());
	}

	public Object visitStringLiteralExp(StringLiteralExp<EClassifier> literal)
	{
		unparseStringLiteralExp(literal);
		return null;
	}

	private void unparseStringLiteralExp(StringLiteralExp<EClassifier> literal)
	{
		String stringText = literal.getStringSymbol();
		stringText = stringText.replace("\\", "\\\\");
		stringText = stringText.replace("'", "\\'");
		stringText = stringText.replace("\r", "\\r");
		stringText = stringText.replace("\n", "\\n");
		stringText = stringText.replace("\t", "\\t");
		addText("'" + stringText + "'");
	}

	public Object visitTupleLiteralExp(TupleLiteralExp<EClassifier, EStructuralFeature> tuple)
	{
		unparseTupleLiteralExp(tuple);
		return null;
	}

	private void unparseTupleLiteralExp(TupleLiteralExp<EClassifier, EStructuralFeature> tuple)
	{
		//		<literal_tuple> ::= 'Tuple' '{' <tuple_item_list>? '}'
		//		<tuple_item_list> ::= <declarator_list>
		addText("Tuple {");
		unparseTupleLiteralParts(tuple.getPart());
		addText("}");
	}

	private void unparseTupleLiteralParts(EList<TupleLiteralPart<EClassifier, EStructuralFeature>> tupleLiteralPartList)
	{
		for (Iterator<TupleLiteralPart<EClassifier, EStructuralFeature>> partItor = tupleLiteralPartList.iterator(); partItor.hasNext();)
		{
			unparseTupleLiteralPart(partItor.next());
			if ( partItor.hasNext() ) addText(",");
		}
	}

	public Object visitTupleLiteralPart(TupleLiteralPart<EClassifier, EStructuralFeature> part)
	{
		unparseTupleLiteralPart(part);
		return null;
	}

	private void unparseTupleLiteralPart(TupleLiteralPart<EClassifier, EStructuralFeature> part)
	{
		addText(part.getName() + ":");
		unparseType(part.getType());
		addText(" = ");
		unparseOCLExpression(part.getValue());
	}

	public Object visitMessageExp(MessageExp<EClassifier, CallOperationAction, SendSignalAction> message)
	{
		unparseMessageExp(message);
		return null;
	}

	private void unparseMessageExp(MessageExp<EClassifier, CallOperationAction, SendSignalAction> message)
	{
		unparseOCLExpression(message.getTarget());
		if ( message.getCalledOperation() != null )
		{
			addText("^^" + message.getCalledOperation().getOperation().getName());
		}
		else
		{
			addText("^^" + message.getSentSignal().getSignal().getName());
		}
		unparseOCLExpressionsCommaSeparatedInParentheses(message.getArgument());
	}

	public Object visitStateExp(StateExp<EClassifier, EObject> state)
	{
		unparseStateExp(state);
		return null;
	}

	private void unparseStateExp(StateExp<EClassifier, EObject> state)
	{
		addText("oclIsInState(" + state.getReferredState() +")");
	}

	public Object visitTypeExp(TypeExp<EClassifier> expression)
	{
		unparseTypeExp(expression);
		return null;
	}

	private void unparseTypeExp(TypeExp<EClassifier> expression)
	{
		unparseType(expression.getReferredType());
	}

	public Object visitUnspecifiedValueExp(UnspecifiedValueExp<EClassifier> unspecExp)
	{
		unparseUnspecifiedValueExp(unspecExp);
		return null;
	}

	private void unparseUnspecifiedValueExp(UnspecifiedValueExp<EClassifier> unspecExp)
	{
		addText("unspecified");
	}

	public Object visitVariableExp(VariableExp<EClassifier, EParameter> variableExp)
	{
		unparseVariableExp(variableExp);
		return null;
	}

	private boolean unparseVariableExp(VariableExp<EClassifier, EParameter> variableExp)
	{
		String variableName = variableExp.getName();
		EClassifier variableType = variableExp.getType();
		Variable<EClassifier,EParameter> referredVariable = variableExp.getReferredVariable();
		
		if ( referredVariable == null )
		{
			addText("/*noReferredVariable*/");
			addText(variableName);
			return true;
		}
		else
		{
			if (variableType instanceof Library )
			{
				String libraryName = variableType.getName();
				addText("/*" + libraryName + "." + variableName + "*/");
				return false;
			}
			else if (variableType instanceof OperationalTransformation )
			{
				String transformationName = variableType.getName();
				if ( variableName.equals("this") )
				{
					addText("/*" + transformationName + "." + variableName + "*/");
					return false;					
				}
				else
				{
					addText(variableName);
					return true;
				}
			}
			else
			{
//				addText("/*VariableExp*/" + variableName);
				addText(variableName);
				return true;
			}
		}
		
	}

	public Object visitCollectionItem(CollectionItem<EClassifier> collectionItem)
	{
		unparseCollectionItem(collectionItem);
		return null;
	}

	private void unparseCollectionItem(CollectionItem<EClassifier> collectionItem)
	{
		unparseOCLExpression(collectionItem.getItem());
	}

	public Object visitCollectionRange(CollectionRange<EClassifier> range)
	{
		unparseCollectionRange(range);
		return null;
	}

	private void unparseCollectionRange(CollectionRange<EClassifier> range)
	{
		unparseOCLExpression(range.getFirst());
		addText("..");
		unparseOCLExpression(range.getLast());
	}

	public Object visitVariable(Variable<EClassifier, EParameter> variable)
	{
		unparseVariable(variable,false);
		return null;
	}

	private void unparseVariable(Variable<EClassifier, EParameter> variable, boolean withInitExpression)
	{
		if ( variable == null ) return;
		
		unparseVariableNameType(variable);
		
		if ( withInitExpression )
		{
			org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = variable.getInitExpression();
			if ( initExpression != null )
			{
				addText(" = ");
				unparseOCLExpression(initExpression);
			}
		}
	}

	private void unparseVariableNameType(Variable<EClassifier, EParameter> variable)
	{
		unparseVariableNameType(variable,true);
	}

	private void unparseVariableNameType(Variable<EClassifier, EParameter> variable, boolean withType)
	{
		String variableName = variable.getName();
		boolean variableNameIsNotGenerated = !variableNameIsGeneratedTemp(variableName);
		if ( variableNameIsNotGenerated )
		{
			addText(variableName);
		}
		else
		{
			addText("/*" + variableName + "*/");
		}

		if ( withType )
		{
			if ( variableNameIsNotGenerated )
			{
				addText(":");
			}
			unparseType(variable.getType());
		}		
	}

	private boolean variableNameIsGeneratedTemp(String variableName)
	{
		boolean isGeneratedTemp =
				variableName.startsWith(QvtEnvironmentBase.GENERATED_NAME_SPECIAL_PREFIX + "temp");
		return isGeneratedTemp;
	}

	public Object visitExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> expression)
	{
		unparseExpressionInOCL(expression);
		return null;
	}

	private void unparseExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> expression)
	{
		addText("[TODO]"); //TODO visitExpressionInOCL
	}

	public Object visitConstraint(Constraint constraint)
	{
		unparseConstraint(constraint);
		return null;
	}

	private void unparseConstraint(Constraint constraint)
	{
		addText("[TODO]"); //TODO visitConstraint
		constraint.getConstrainedElements();
		constraint.getSpecification();
		constraint.getStereotype();
	}

	public Object visitContextualProperty(ContextualProperty contextualProperty)
	{
		unparseContextualProperty(contextualProperty);
		return null;
	}

	private void unparseContextualProperty(ContextualProperty contextualProperty)
	{
//		contextualProperty.getOverridden();
		String readonlyText = contextualProperty.isChangeable() ? "" : "readonly ";

		EClass contextClass = contextualProperty.getContext();
		String contextClassText = constructFullyQualifiedType(contextClass);
		
		String propertyName = contextualProperty.getName();
		addText("intermediate " + readonlyText + "property " + contextClassText + "::" + propertyName + ": ");
		unparseType(contextualProperty.getEType());
		
		org.eclipse.ocl.expressions.OCLExpression<EClassifier> initExpression = contextualProperty.getInitExpression();
		if ( initExpression != null )
		{
			addText(" = ");
			unparseOCLExpression(initExpression);
		}
		
		addText(";");
		addLineBreak();
	}

	public Object visitVarParameter(VarParameter parameter)
	{
		unparseVarParameter(parameter);
		return null;
	}

	private void unparseVarParameter(VarParameter parameter)
	{
		if ( parameter instanceof MappingParameter )
		{
			unparseMappingParameter((MappingParameter)parameter,true);
		}
		else
		{
			unparseVarParameter(parameter,true);
		}
	}
	
	private void unparseVarParameter(VarParameter parameter, boolean withDirectionAndName)
	{
		String direction = parameter.getKind().getLiteral();
		String name = parameter.getName();
		String directionAndName = withDirectionAndName ? "" + direction + " " + name + " : " : "";
		
		addText(directionAndName);
		
		unparseVarParameterType(parameter.getType());
	}
	
	private void unparseVarParameterType(EClassifier parameterType)
	{
		if (parameterType instanceof CollectionType )
		{
			unparseVarParameterCollectionType((CollectionType)parameterType);
		}
		else
		{
			unparseType(parameterType);
		}
	}
	
	private void unparseVarParameterCollectionType(CollectionType collectionType)
	{
		if ( collectionType instanceof ListType ) {
			addText("List (");
		}
		else if ( collectionType instanceof SequenceType ) {
			addText("Sequence (");
		}
		else if ( collectionType instanceof OrderedSetType ) {
			addText("OrderedSet (");
		}
		else if ( collectionType instanceof SetType ) {
			addText("Set (");
		}
		else if ( collectionType instanceof BagType ) {
			addText("Bag (");
		}
		else if ( collectionType instanceof DictionaryType ) {
			addText("Dict (");
			DictionaryType dictionaryType = (DictionaryType) collectionType;
			unparseVarParameterType(dictionaryType.getKeyType());
			addText(",");
		}
		else
		{
			addText("Collection (");
		}
		
		unparseVarParameterType(collectionType.getElementType());
		
		addText(")");
	}

	private void unparseVarParameters(EList<VarParameter> parameters, boolean withDirectionAndName)
	{
		if ( parameters == null || parameters.size() == 0 ) return;
		
		addText(" : ");
		
		if ( parameters.size() == 1 )
		{
			withDirectionAndName = false;
		}
		
		for (Iterator<VarParameter> iterator = parameters.iterator(); iterator.hasNext();)
		{
			VarParameter parameter = iterator.next();
			unparseVarParameter(parameter,withDirectionAndName);
			if ( iterator.hasNext() )
			{
				addText(", ");
			}
		}
	}
	
	private String escapeKeywordName(String name)
	{
		if ( QvtKeywords.KEYWORDS.contains(name) )
		{
			return "_" + name;
		}
		else
		{
			return name;
		}
	}
	
	/**
	 * Handling of generated text fragments
	 */
	
	private List<String> lines = new ArrayList<String>();
	private StringBuilder currentLine = new StringBuilder(1024);
	
	private int indentationLevel = 0;
	private String[] indentation = {
			"", "\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t",
			"\t\t\t\t\t\t", "\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t",
			"\t\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t\t"
			};
	
	private void indent()
	{
		indentationLevel++;
	}
	
	private void unindent()
	{
		if ( indentationLevel > 0 )
			indentationLevel--;
	}
	
	public  List<String> getLines()
	{
		if ( currentLine.length() > 0 )
		{
			addLineBreak();
		}
		return lines;
	}
	
	private void addText(String text)
	{
		if ( text == null || text.length() == 0 ) return;
		
		if ( currentLine.length() == 0 )
		{
			currentLine.append(indentation[indentationLevel]);
		}
		currentLine.append(text);
	}
	
	private void addTextAndIndent(String text)
	{
		addText(text);
		indent();
	}
	
	private void unindentAndAddText(String text)
	{
		unindent();
		addText(text);
	}
	
	private void addLineBreak()
	{
		lines.add(currentLine.toString());
		currentLine.setLength(0);
	}

	private void addEmptyLine()
	{
		if ( currentLine.length() > 0 )
		{
			addLineBreak();
		}
		addLineBreak();
	}


	/*
	 * Notes on QVToParser issues
	 * 
	 * Helper.isQuery not set --> bug489092
	 * Library.isBlackbox not set
	 * MappingCallExp.setOperationCode --> bug489093
	 * CollectionLiteralExp.kind always "Set": worked around in this Unparser
	 */
}
