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
 *     Christopher Gerking - bug 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.internal.qvt.oml.Transformation;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.emf.util.ModelContent;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.config.QvtConfigurationProperty;

public interface QvtTransformation extends Transformation {

	public interface TransformationParameter {
		public enum DirectionKind {
			IN,
			INOUT,
			OUT;
		}

		DirectionKind getDirectionKind();
		
		/**
		 * 
		 * @return name of transformation parameter, may be empty
		 */
		String getName();

		/**
		 * 
		 * @return list of model type's metamodels
		 */
		List<EPackage> getMetamodels();
		
		/**
		 * 
		 * @return model type name
		 */
		String getModelTypeName();
		
		/**
		 * 
		 * @return type of appropriate parameter declaration in main(), may be <b>null</b>
		 */
		EClassifier getEntryType();
		
		/**
		 * 
		 * @return name of appropriate parameter declaration in main()
		 */
		String getEntryName();
	}
	
    String getModuleName() throws MdaException;

    Set<QvtConfigurationProperty> getConfigurationProperties() throws MdaException;

    List<TransformationParameter> getParameters() throws MdaException;
    
    boolean hasEntryOperation() throws MdaException;
    
    boolean isBlackbox() throws MdaException;
    
    @Deprecated
    org.eclipse.m2m.internal.qvt.oml.runtime.generator.TransformationRunner.Out run(org.eclipse.m2m.internal.qvt.oml.runtime.generator.TransformationRunner.In in) throws MdaException;
    
    @Deprecated
    ModelContent loadInput(URI inputObjectURI) throws MdaException;    
}
