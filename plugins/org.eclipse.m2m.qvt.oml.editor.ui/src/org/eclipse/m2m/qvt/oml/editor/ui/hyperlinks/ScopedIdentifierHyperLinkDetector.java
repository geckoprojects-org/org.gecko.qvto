/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.editor.ui.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.m2m.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.qvt.oml.editor.ui.CSTHelper;
import org.eclipse.m2m.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.qvt.oml.internal.cst.MappingRuleCS;
import org.eclipse.m2m.qvt.oml.internal.cst.temp.ScopedNameCS;
import org.eclipse.ocl.cst.CSTNode;


public class ScopedIdentifierHyperLinkDetector implements IHyperlinkDetectorHelper {

	public IHyperlink detectHyperlink(IDetectionContext context) {		
		CSTNode element = context.getSyntaxElement();
		
		if(element instanceof ScopedNameCS) {
			MappingOperation mapping = ASTBindingHelper.resolveASTNode(element, MappingOperation.class);
			MappingRuleCS mappingCS = ASTBindingHelper.resolveCSTNode(mapping, MappingRuleCS.class);
			
			CSTNode node = mappingCS.getMappingDeclarationCS().getSimpleNameCS();
			IRegion destRegion = new Region(node.getStartOffset(), node.getEndOffset() - node.getStartOffset() + 1);
			IRegion reg = new Region(element.getStartOffset(), element.getEndOffset() - element.getStartOffset() + 1);
			
			return new QvtFileHyperlink(reg,
					CSTHelper.getSourceFile(node), destRegion, destRegion);
		}
		
		return null;
	}
}
