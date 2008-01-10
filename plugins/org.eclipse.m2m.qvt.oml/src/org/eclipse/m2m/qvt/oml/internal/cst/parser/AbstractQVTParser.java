/*******************************************************************************
 * Copyright (c) 2007 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.internal.cst.parser;

import lpg.lpgjavaruntime.IToken;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.m2m.qvt.oml.ast.environment.QvtOperationalEnv;
import org.eclipse.m2m.qvt.oml.internal.cst.AssertExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.AssignStatementCS;
import org.eclipse.m2m.qvt.oml.internal.cst.BlockExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ConfigPropertyCS;
import org.eclipse.m2m.qvt.oml.internal.cst.DirectionKindCS;
import org.eclipse.m2m.qvt.oml.internal.cst.DirectionKindEnum;
import org.eclipse.m2m.qvt.oml.internal.cst.ElementWithBody;
import org.eclipse.m2m.qvt.oml.internal.cst.ExpressionStatementCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ImportCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ImportKindEnum;
import org.eclipse.m2m.qvt.oml.internal.cst.LibraryCS;
import org.eclipse.m2m.qvt.oml.internal.cst.LibraryImportCS;
import org.eclipse.m2m.qvt.oml.internal.cst.LocalPropertyCS;
import org.eclipse.m2m.qvt.oml.internal.cst.LogExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingBodyCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingCallExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingDeclarationCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingEndCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingInitCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingMethodCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingModuleCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingQueryCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingRuleCS;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingSectionCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModelTypeCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModuleImportCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModuleKindCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModuleKindEnum;
import org.eclipse.m2m.qvt.oml.internal.cst.ModulePropertyCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModuleRefCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ModuleUsageCS;
import org.eclipse.m2m.qvt.oml.internal.cst.OutExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.PackageRefCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ParameterDeclarationCS;
import org.eclipse.m2m.qvt.oml.internal.cst.PatternPropertyExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.RenameCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ResolveExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.ResolveInExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.StatementCS;
import org.eclipse.m2m.qvt.oml.internal.cst.SwitchAltExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.SwitchExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.TransformationHeaderCS;
import org.eclipse.m2m.qvt.oml.internal.cst.TransformationRefineCS;
import org.eclipse.m2m.qvt.oml.internal.cst.TypeSpecCS;
import org.eclipse.m2m.qvt.oml.internal.cst.VariableInitializationCS;
import org.eclipse.m2m.qvt.oml.internal.cst.WhileExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.temp.ErrorOutExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.temp.ResolveOpArgsExpCS;
import org.eclipse.m2m.qvt.oml.internal.cst.temp.ScopedNameCS;
import org.eclipse.m2m.qvt.oml.internal.cst.temp.TempFactory;
import org.eclipse.ocl.cst.CSTFactory;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.OCLExpressionCS;
import org.eclipse.ocl.cst.PathNameCS;
import org.eclipse.ocl.cst.SimpleNameCS;
import org.eclipse.ocl.cst.SimpleTypeEnum;
import org.eclipse.ocl.cst.StringLiteralExpCS;
import org.eclipse.ocl.cst.TypeCS;
import org.eclipse.ocl.cst.VariableCS;
import org.eclipse.ocl.lpg.BasicEnvironment;
import org.eclipse.ocl.parser.AbstractOCLParser;

public abstract class AbstractQVTParser extends AbstractOCLParser {

	protected static class Empty {		
		private static final EList<?> EMPTY_LIST_INSTANCE = new BasicEList.UnmodifiableEList<Object>(0, new Object[0]);
		
		@SuppressWarnings("unchecked")
		static final <T> EList<T> list() { 
			return (EList<T>)EMPTY_LIST_INSTANCE;	
		}
	}
	
	
	protected AbstractQVTParser(BasicEnvironment environment) {
		super(environment);
	}

	protected AbstractQVTParser(QvtOpLexer lexStream) {
		super(lexStream);
	}
	
	@Override
	public QvtOperationalEnv getEnvironment() {	
		return (QvtOperationalEnv) super.getEnvironment();
	}
	 		
	@Override
	public QvtOpLexer getLexer() {
		return (QvtOpLexer) super.getLexStream();
	} 
	
	
	public abstract String getTokenKindName(int kind);
	
	protected final void setBodyOffsets(ElementWithBody element, CSTNode start, IToken end) {
		element.setBodyStartLocation(start.getEndOffset());
		element.setBodyEndLocation(end.getStartOffset());
	}
	
	protected final CSTNode createLocalPropertyCS(IToken tokenText, TypeCS sym,
			OCLExpressionCS sym2) {
				LocalPropertyCS prop = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createLocalPropertyCS();
				SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
				setOffsets(nameCS, tokenText);
				prop.setSimpleNameCS(nameCS);
				prop.setTypeCS(sym);
				prop.setOclExpressionCS(sym2);
				return prop;
			}

	protected final CSTNode createConfigPropertyCS(IToken tokenText, TypeCS sym) {
		ConfigPropertyCS prop = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createConfigPropertyCS();
		SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
		setOffsets(nameCS, tokenText);
		prop.setSimpleNameCS(nameCS);
		prop.setTypeCS(sym);
		return prop;
	}

	protected final CSTNode createRenameCS(TypeCS sym, IToken tokenText, StringLiteralExpCS sym2) {
		RenameCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createRenameCS();
		SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
		setOffsets(nameCS, tokenText);
		result.setSimpleNameCS(nameCS);
		result.setTypeCS(sym);
		result.setOriginalName(sym2);
		return result;
	}

	protected final CSTNode createModuleImportCS(PathNameCS sym) {
		ModuleImportCS imp = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createModuleImportCS();
		imp.setPathNameCS(sym);
		return imp;
	}

	protected final CSTNode createLibraryCS(PathNameCS name, EList<ImportCS> imports, EList<ModelTypeCS> metamodels,
			EList<RenameCS> renamings, EList<ModulePropertyCS> properties, EList<MappingMethodCS> methods) {
				LibraryCS imp = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createLibraryCS();
				TransformationHeaderCS headerCS = createTransformationHeaderCS(
						Empty.<StringLiteralExpCS>list(), name, 
						Empty.<ParameterDeclarationCS>list(), 
						Empty.<ModuleUsageCS>list(), null);
				setOffsets(headerCS, name);
				initializeModule(imp, headerCS, imports, metamodels, renamings, properties, methods);
				return imp;
			}

	protected final CSTNode createMappingModuleCS(PathNameCS name, EList<ImportCS> imports,
			EList<ModelTypeCS> metamodels, EList<RenameCS> renamings, EList<ModulePropertyCS> properties, 
				EList<MappingMethodCS> methods) {
				MappingModuleCS imp = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingModuleCS();
				TransformationHeaderCS headerCS = createTransformationHeaderCS(
						Empty.<StringLiteralExpCS>list(), name, 
						Empty.<ParameterDeclarationCS>list(), 
						Empty.<ModuleUsageCS>list(), null);
				setOffsets(headerCS, name);
				initializeModule(imp, headerCS, imports, metamodels, renamings, properties, methods);
				return imp;
			}

	protected final CSTNode createMappingModuleCS(TransformationHeaderCS header, EList<ImportCS> imports,
			EList<ModelTypeCS> metamodels, EList<RenameCS> renamings, EList<ModulePropertyCS> properties, 
			EList<MappingMethodCS> methods) {
				MappingModuleCS imp = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingModuleCS();
				initializeModule(imp, header, imports, metamodels, renamings, properties, methods);
				return imp;
			}

	private void initializeModule(MappingModuleCS result, TransformationHeaderCS header, EList<ImportCS> imports,
			EList<ModelTypeCS> metamodels, EList<RenameCS> renamings, EList<ModulePropertyCS> properties, 
			EList<MappingMethodCS> methods) {
				result.setHeaderCS(header);
				result.getImports().addAll(imports);
				result.getMetamodels().addAll(metamodels);
				result.getRenamings().addAll(renamings);
				result.getProperties().addAll(properties);
				result.getMethods().addAll(methods);
			}

	protected final MappingQueryCS createMappingQueryCS(MappingDeclarationCS sym, EList<OCLExpressionCS> sym2) {
		MappingQueryCS query = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingQueryCS();
		query.setMappingDeclarationCS(sym);
		query.getExpressions().addAll(sym2);
		return query;
	}

	protected final CSTNode createMappingRuleCS(MappingDeclarationCS mappingDecl, OCLExpressionCS guard,
			MappingInitCS mappingInit, MappingBodyCS mappingBody, MappingEndCS mappingEnd) {
				MappingRuleCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingRuleCS();
				result.setMappingDeclarationCS(mappingDecl);
				result.setGuard(guard);
				result.setMappingInitCS(mappingInit);
				if (mappingInit != null) {
					mappingInit.setMappingRuleCS(result);
				}
				result.setMappingBodyCS(mappingBody);
				result.setMappingEndCS(mappingEnd);
				if (mappingEnd != null) {
					mappingEnd.setMappingRuleCS(result);
				}
				return result;
			}

	protected final CSTNode createLibraryImportCS(PathNameCS sym) {
		LibraryImportCS imp = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createLibraryImportCS();
		imp.setPathNameCS(sym);
		return imp;
	}

	protected final CSTNode createVariableInitializationCS(IToken tokenText, TypeCS sym,
			OCLExpressionCS sym2) {
				VariableInitializationCS result = (tokenText.getKind() == QvtOpLPGParsersym.TK_ERROR_TOKEN)
					? TempFactory.eINSTANCE.createErrorVariableInitializationCS()
					: org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createVariableInitializationCS();
				SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
				setOffsets(nameCS, tokenText);
				result.setSimpleNameCS(nameCS);
				result.setTypeCS(sym);
				result.setOclExpressionCS(sym2);
				return result;
			}

	protected final CSTNode createExpressionStatementCS(OCLExpressionCS sym) {
		ExpressionStatementCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createExpressionStatementCS();
		result.setOclExpressionCS(sym);
		return result;
	}

	protected final CSTNode createMappingEndCS(EList<StatementCS> statements, int endOffset, int startOffset) {
		MappingEndCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingEndCS();
		initializeMappingSection(result, statements, endOffset, startOffset);		
		return result;
	}

	protected final CSTNode createMappingInitCS(EList<StatementCS> statements, int endOffset, int startOffset) {
		MappingInitCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingInitCS();
		initializeMappingSection(result, statements, endOffset, startOffset);		
		return result;
	}

	private void initializeMappingSection(MappingSectionCS section, EList<StatementCS> statements,
			int endOffset, int startOffset) {
				section.getStatements().addAll(statements);
				section.setBodyStartLocation(startOffset);
				section.setBodyEndLocation(endOffset);
			}

	protected final CSTNode createParameterDeclarationCS(DirectionKindCS sym, IToken tokenText,
			TypeSpecCS typeSpecCS) {
				ParameterDeclarationCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createParameterDeclarationCS();
				SimpleNameCS nameCS;
				if (tokenText != null) {
					nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
					setOffsets(nameCS, tokenText);
				}
				else {
					nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, "");
				}
				result.setSimpleNameCS(nameCS);
				result.setTypeSpecCS(typeSpecCS);
				if (sym != null) {
					result.setDirectionKind(sym.getDirectionKind());
				}
				return result;
			}

	protected final TypeSpecCS createTypeSpecCS(TypeCS typeCS, IToken extentLocation) {
		TypeSpecCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createTypeSpecCS();
		result.setTypeCS(typeCS);
		setOffsets(result, typeCS);
		if (extentLocation != null) {
			SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, extentLocation.toString());
			setOffsets(nameCS, extentLocation);
			result.setSimpleNameCS(nameCS);
			result.setEndOffset(extentLocation.getEndOffset());
		}
		return result;
	}

	protected final CSTNode createDirectionKindCS(DirectionKindEnum kind) {
		DirectionKindCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createDirectionKindCS();
		result.setDirectionKind(kind);
		return result;
	}

	protected final CSTNode createMappingDeclarationCS(DirectionKindCS directionKindCS, ScopedNameCS scopedNameCS,
			EList<ParameterDeclarationCS> parameters, TypeSpecCS returnType) {
				MappingDeclarationCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingDeclarationCS();
				result.setDirectionKindCS(directionKindCS);
				result.setReturnType(returnType);
				result.getParameters().addAll(parameters);
				result.setSimpleNameCS(createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, scopedNameCS.getName()));
			
				SimpleNameCS name = result.getSimpleNameCS();
				if(name != null) {
					int endOffset = scopedNameCS.getEndOffset();
					int length = (name.getValue() != null) ?  name.getValue().length() : 0;			
					name.setStartOffset(endOffset - (length > 0 ? length-1 : 0));
					name.setEndOffset(endOffset);
				}		
			
				result.setContextType(scopedNameCS.getTypeCS());
				return result;
			}

	private PathNameCS createParentPath(PathNameCS pathNameCS) {
		PathNameCS result = CSTFactory.eINSTANCE.createPathNameCS();
		result.getSequenceOfNames().addAll(pathNameCS.getSequenceOfNames());
		result.setStartOffset(pathNameCS.getStartOffset());
		if (result.getSequenceOfNames().size() > 0) {
			String lastSegment = result.getSequenceOfNames().remove(result.getSequenceOfNames().size()-1);
			result.setEndOffset(pathNameCS.getEndOffset()-lastSegment.length());
		}
		else {
			result.setEndOffset(pathNameCS.getEndOffset());
		}
		return result;
	}

	protected final CSTNode createPatternPropertyCS(IToken tokenText, OCLExpressionCS sym,
			boolean b) {
				PatternPropertyExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createPatternPropertyExpCS();
				SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, tokenText.toString());
				setOffsets(nameCS, tokenText);
				result.setSimpleNameCS(nameCS);
				result.setOclExpressionCS(sym);
				result.setIncremental(b);
				return result;
			}

	protected final CSTNode createAssignStatementCS(OCLExpressionCS sym, OCLExpressionCS sym2,
			boolean b) {
				AssignStatementCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createAssignStatementCS();
				result.setLValueCS(sym);
				result.setOclExpressionCS(sym2);
				result.setIncremental(b);
				return result;
			}

	protected final void updateMappingBodyPositions(MappingBodyCS mappingBody, int bodyLeft,
			int bodyRight, int outBodyLeft, int outBodyRight) {
				if (mappingBody != null && mappingBody.isShort() && mappingBody.getOutExpCS() != null) {
					OutExpCS outExp = mappingBody.getOutExpCS();
					outExp.setStartOffset(bodyLeft);
					outExp.setEndOffset(bodyRight);
					outExp.setBodyStartLocation(outBodyLeft);
					outExp.setBodyEndLocation(outBodyRight);
					mappingBody.setStartOffset(bodyLeft);
					mappingBody.setEndOffset(bodyRight);
				}
			}

	protected final OutExpCS createOutExpCS(OutExpCS result, EList<OCLExpressionCS> expressions, TypeSpecCS typeSpecCS,
			int startOffset, int endOffset) {
				result.setTypeSpecCS(typeSpecCS);
				result.getExpressions().addAll(expressions);
				result.setBodyStartLocation(startOffset);
				result.setBodyEndLocation(endOffset);
				return result;
			}

	protected final OutExpCS createOutExpCS(EList<OCLExpressionCS> expressions, TypeSpecCS typeSpecCS, int startOffset,
			int endOffset) {
				OutExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createOutExpCS();
				return createOutExpCS(result, expressions, typeSpecCS, startOffset, endOffset);
			}

	protected final OutExpCS createErrorOutExpCS(EList<OCLExpressionCS> expressions, TypeSpecCS typeSpecCS,
			int startOffset, int endOffset, int fullStartOffset, int fullEndOffset) {
				ErrorOutExpCS result = TempFactory.eINSTANCE.createErrorOutExpCS();
				result.setFullStartOffset(fullStartOffset);
				result.setFullEndOffset(fullEndOffset);
				return createOutExpCS(result, expressions, typeSpecCS, startOffset, endOffset);
			}

	protected final MappingBodyCS createMappingBodyCS(OutExpCS sym, boolean b) {
		MappingBodyCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingBodyCS();
		result.setOutExpCS(sym);
		result.setShort(b);
		return result;
	}

	protected final CSTNode createMappingCallExpCS(SimpleNameCS sym, EList<OCLExpressionCS> arguments,
			boolean b) {
				MappingCallExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingCallExpCS();
				result.setSimpleNameCS(sym);
				result.getArguments().addAll(arguments);
				result.setStrict(b);
				result.setIsMarkedPreCS(CSTFactory.eINSTANCE.createIsMarkedPreCS());
				return result;
			}

	protected final CSTNode createMappingCallExpCS(PathNameCS sym, EList<OCLExpressionCS> arguments,
			boolean b) {
				MappingCallExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createMappingCallExpCS();
				if (sym.getSequenceOfNames().size() > 1) {
					result.setSource(createParentPath(sym));
				}
				if (sym.getSequenceOfNames().size() > 0) {
					String lastSegment = sym.getSequenceOfNames().get(sym.getSequenceOfNames().size()-1);
					SimpleNameCS nameCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, lastSegment);
					nameCS.setStartOffset(sym.getEndOffset()-lastSegment.length() + 1);
					nameCS.setEndOffset(sym.getEndOffset());
					result.setSimpleNameCS(nameCS);
				}		
				result.getArguments().addAll(arguments);
				result.setStrict(b);
				result.setIsMarkedPreCS(CSTFactory.eINSTANCE.createIsMarkedPreCS());
				return result;
			}

	protected final CSTNode createResolveOpArgsExpCS(IToken target, TypeCS typeCS,
			OCLExpressionCS condition) {
			    ResolveOpArgsExpCS resolveOpArgsExpCS = TempFactory.eINSTANCE.createResolveOpArgsExpCS();
			    VariableCS variableCS = CSTFactory.eINSTANCE.createVariableCS();
			    variableCS.setTypeCS(typeCS);
			    if (target == null) {
					setOffsets(variableCS, typeCS);
			    } else {
			        variableCS.setName(target.toString());
					setOffsets(variableCS, target, typeCS);
			    }
			    resolveOpArgsExpCS.setTarget(variableCS);
			    resolveOpArgsExpCS.setCondition(condition);
			    return resolveOpArgsExpCS;
			}

	protected final CSTNode createResolveExpCS(IToken lateToken, IToken opCode, ResolveOpArgsExpCS resolveOpArgsExpCS) {
	    return populateResolveExpCS(org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createResolveExpCS(), 
	            lateToken, opCode, resolveOpArgsExpCS);
	}

	protected final CSTNode createResolveInExpCS(IToken lateToken, IToken opCode,
			ScopedNameCS mapping, ResolveOpArgsExpCS resolveOpArgsExpCS) {
				ResolveInExpCS resolveInExpCS = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createResolveInExpCS();
				resolveInExpCS.setInMappingType(mapping.getTypeCS());
				resolveInExpCS.setInMappingName(mapping.getName());
				return populateResolveExpCS(resolveInExpCS, lateToken, opCode, resolveOpArgsExpCS);
			}

	protected final CSTNode populateResolveExpCS(ResolveExpCS resolveExpCS, IToken lateToken,
			IToken opCode, ResolveOpArgsExpCS resolveOpArgsExpCS) {
			    resolveExpCS.setIsDeferred((lateToken != null) && getTokenKindName(QvtOpLPGParsersym.TK_late).equals(lateToken.toString()));
			    String opCodeText = opCode.toString();
			    resolveExpCS.setOne(opCodeText.indexOf("one") > 0); //$NON-NLS-1$
			    resolveExpCS.setIsInverse(opCodeText.indexOf("inv") == 0); //$NON-NLS-1$
			    if (resolveOpArgsExpCS != null) {
			        resolveExpCS.setTarget(resolveOpArgsExpCS.getTarget());
			        resolveExpCS.setCondition(resolveOpArgsExpCS.getCondition());
			    }
			    return resolveExpCS;
			}

	protected final ScopedNameCS createScopedNameCS(TypeCS typeCS, String name) {
		ScopedNameCS result = TempFactory.eINSTANCE.createScopedNameCS();
		result.setTypeCS(typeCS);
		result.setName(name);
		return result;
	}

	protected CSTNode createLegacyWhileExpCS(OCLExpressionCS cond, OCLExpressionCS res, EList<OCLExpressionCS> expressions) {
		WhileExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createWhileExpCS();
		result.setCondition(cond);
		result.setResult(res);
		BlockExpCS body = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createBlockExpCS();
		body.getBodyExpressions().addAll(expressions);
		result.setBody(body);
		return result;
	}
	
	protected CSTNode createWhileExpCS(VariableCS resultVar, OCLExpressionCS cond, EList<OCLExpressionCS> expressions) {
		WhileExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createWhileExpCS();
		result.setCondition(cond);
		result.setResultVar(resultVar);
		BlockExpCS body = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createBlockExpCS();
		body.getBodyExpressions().addAll(expressions);
		result.setBody(body);
		return result;
	}		
	
	protected final CSTNode createLogExpCS(EList<OCLExpressionCS> args, OCLExpressionCS condition) {
		LogExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createLogExpCS();
		String name = getTokenKindName(QvtOpLPGParsersym.TK_log);
		result.setSimpleNameCS(createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, name));

		result.getArguments().addAll(args);		
		result.setCondition(condition);		
		return result;
	}
	
	protected final CSTNode createAssertExpCS(OCLExpressionCS assertCondition, SimpleNameCS severityIdentifier, LogExpCS logExpCS) {
		AssertExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createAssertExpCS();
		result.setAssertion(assertCondition);
		result.setSeverity(severityIdentifier);
		result.setLog(logExpCS);
		return result;
	}
	

	protected final CSTNode createSwitchExpCS(EList<SwitchAltExpCS> altExps, OCLExpressionCS elseExp) {
		SwitchExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createSwitchExpCS();
		result.getAlternativePart().addAll(altExps);
		result.setElsePart(elseExp);
		return result;
	}

	protected final CSTNode createSwitchAltExpCS(OCLExpressionCS cond, OCLExpressionCS body) {
		SwitchAltExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createSwitchAltExpCS();
		result.setCondition(cond);
		result.setBody(body);
		return result;
	}

	protected final CSTNode createBlockExpCS(EList<OCLExpressionCS> expressions) {
		BlockExpCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createBlockExpCS();
		result.getBodyExpressions().addAll(expressions);
		return result;
	}

	protected final ModelTypeCS createModelTypeCS(IToken identifier, StringLiteralExpCS complianceKind, 
			EList<PackageRefCS> packageRefList, EList<StatementCS> whereList) {
				ModelTypeCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createModelTypeCS();
				SimpleNameCS identifierCS = createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL,
							(identifier.getPrsStream() == null ? "" : identifier.toString()));
				identifierCS.setStartOffset(identifier.getStartOffset());
				identifierCS.setEndOffset(identifier.getEndOffset());
				result.setIdentifierCS(identifierCS);
				result.setComplianceKindCS(complianceKind);
				result.getPackageRefs().addAll(packageRefList);
				result.getWhereStatements().addAll(whereList);		
				return result;
			}

	protected final CSTNode createPackageRefCS(PathNameCS scopedIdentifierCS, StringLiteralExpCS uriCS) {
		PackageRefCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createPackageRefCS();
		result.setPathNameCS(scopedIdentifierCS);
		result.setUriCS(uriCS);
		return result;
	}

	protected final CSTNode createModuleKindCS(ModuleKindEnum moduleKind) {
		ModuleKindCS moduleKindCS = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createModuleKindCS();
		moduleKindCS.setModuleKind(moduleKind);
		return moduleKindCS;
	}

	protected final CSTNode createModuleRefCS(PathNameCS pathNameCS, EList<ParameterDeclarationCS> params) {
		ModuleRefCS moduleRefCS = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createModuleRefCS();
		moduleRefCS.setPathNameCS(pathNameCS);
		moduleRefCS.getParameters().addAll(params);
		return moduleRefCS;
	}

	protected final CSTNode createModuleUsageCS(ImportKindEnum importKind, ModuleKindCS moduleKindCS, EList<ModuleRefCS> moduleRefs) {
		ModuleUsageCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createModuleUsageCS();
		result.setImportKind(importKind);
		result.setModuleKindCS(moduleKindCS);
		result.getModuleRefs().addAll(moduleRefs);
		return result;
	}

	protected final CSTNode createTransformationRefineCS(ModuleRefCS moduleRefCS, IToken token) {
		TransformationRefineCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createTransformationRefineCS();
		result.setModuleRefCS(moduleRefCS);
		result.setSimpleNameCS(createSimpleNameCS(SimpleTypeEnum.IDENTIFIER_LITERAL, token.toString()));
		return result;
	}

	protected final TransformationHeaderCS createTransformationHeaderCS(EList<StringLiteralExpCS> qualifiers, 
			PathNameCS pathNameCS, EList<ParameterDeclarationCS> params, EList<ModuleUsageCS> transfUsages, 
			TransformationRefineCS transfRefineCS) {
				TransformationHeaderCS result = org.eclipse.m2m.qvt.oml.internal.cst.CSTFactory.eINSTANCE.createTransformationHeaderCS();
				result.getQualifiers().addAll(qualifiers);
				result.getParameters().addAll(params);
				result.getModuleUsages().addAll(transfUsages);
				result.setTransformationRefineCS(transfRefineCS);
				result.setPathNameCS(pathNameCS);
				return result;
			}

	protected final int getEndOffset(IToken token, EList<?>... listOpt) {
		int offset = token.getEndOffset();
		for (EList<?> list : listOpt) {
			if (list.size() == 0) {
				continue;
			}
			CSTNode node = (CSTNode) list.get(list.size()-1);
			offset = node.getEndOffset();
		}
		return offset;
	}

}