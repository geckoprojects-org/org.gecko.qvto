/*******************************************************************************
 * Copyright (c) 2007, 2019 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 537609
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.launch;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.common.launch.BaseProcess;
import org.eclipse.m2m.internal.qvt.oml.common.launch.BaseProcess.IRunnable;
import org.eclipse.m2m.internal.qvt.oml.common.launch.SafeRunner;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.StatusUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.WorkspaceUtils;
import org.eclipse.m2m.internal.qvt.oml.runtime.launch.QvtValidator.ValidationType;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtModule;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.TransformationUtil;
import org.eclipse.m2m.internal.qvt.oml.runtime.util.MiscUtil;

public abstract class QvtLaunchConfigurationDelegateBase extends LaunchConfigurationDelegate {
	
    @Override
	protected IProject[] getProjectsForProblemSearch(ILaunchConfiguration configuration, String mode) throws CoreException {
    	IFile moduleFile = getModuleFile(configuration);
    	if (moduleFile == null || !moduleFile.exists()) {
    		return new IProject[0];
    	}
        return new IProject[] { moduleFile.getProject() };
    }
 
    public static QvtModule getQvtModule(ILaunchConfiguration configuration) throws CoreException {
		try {
	        String moduleUri = QvtLaunchUtil.getTransformationURI(configuration);
			return TransformationUtil.getQvtModule(EmfUtil.makeUri(moduleUri));
		} catch (MdaException e) {
        	IStatus errorStatus = MiscUtil.makeErrorStatus(e.getMessage());
        	throw new CoreException(errorStatus);
		}
    }

    public static IFile getModuleFile(ILaunchConfiguration configuration) throws CoreException {
        String moduleFileName = QvtLaunchUtil.getTransformationURI(configuration);
        URI moduleUri = EmfUtil.makeUri(moduleFileName);
        IFile moduleFile = moduleUri != null ? WorkspaceUtils.getWorkspaceFile(moduleUri) : null;
        if(moduleFile == null) {
        	//IStatus errorStatus = MiscUtil.makeErrorStatus( 
        		//	NLS.bind(Messages.QvtLaunchConfigurationDelegate_transformationFileNotFound, moduleFileName));
        	//throw new CoreException(errorStatus);
        }
        
        return moduleFile;
    }

    public static IStatus validate(QvtTransformation transformation, ILaunchConfiguration configuration) throws CoreException {
    	List<TargetUriData> targetUris = QvtLaunchUtil.getTargetUris(configuration);
        String traceFile = QvtLaunchUtil.getTraceFileURI(configuration);
        boolean useTraceFile = QvtLaunchUtil.shouldGenerateTraceFile(configuration); 
        boolean isIncrementalUpdate = QvtLaunchUtil.isIncrementalUpdate(configuration); 
        
        try {
        	return QvtValidator.validateTransformation(transformation, targetUris, traceFile, useTraceFile,
        			isIncrementalUpdate, ValidationType.FULL_VALIDATION);
        }
        catch (MdaException ex) {
        	throw new CoreException(StatusUtil.makeErrorStatus(ex.getMessage(), ex));
        }
    }
    
    @Deprecated
    public static BaseProcess.IRunnable getSafeRunnable(QvtTransformation transformation, IRunnable r) throws CoreException {
        return SafeRunner.getSafeRunnable(r);
    }    
}
