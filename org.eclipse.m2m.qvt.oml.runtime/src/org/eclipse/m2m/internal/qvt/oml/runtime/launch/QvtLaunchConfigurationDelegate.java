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
 *     Christopher Gerking - bugs 431082, 537609
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.common.launch.ProcessJob;
import org.eclipse.m2m.internal.qvt.oml.common.launch.StreamsProxy;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.internal.qvt.oml.runtime.QvtRuntimePlugin;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtInterpretedTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation;
import org.eclipse.m2m.qvt.oml.ExecutionContext;
import org.eclipse.m2m.qvt.oml.util.WriterLog;

public class QvtLaunchConfigurationDelegate extends QvtLaunchConfigurationDelegateBase {

	public static final String LAUNCH_CONFIGURATION_TYPE_ID = QvtPlugin.ID + ".QvtTransformation"; //$NON-NLS-1$

	public QvtLaunchConfigurationDelegate() {
	}

	// FIXME - do refactoring of this area
	public void launch(final ILaunchConfiguration configuration, String mode, final ILaunch launch, final IProgressMonitor launchMonitor) throws CoreException {

		final QvtTransformation qvtTransformation = new QvtInterpretedTransformation(getQvtModule(configuration));

		final StreamsProxy streamsProxy = new StreamsProxy();

		ProcessJob processJob = new ProcessJob(Messages.InMemoryQvtLaunchConfigurationDelegate_TransformationJobName, launch, streamsProxy) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				try {
					IStatus status = QvtLaunchConfigurationDelegateBase.validate(qvtTransformation, configuration);
					if(status.getSeverity() > IStatus.WARNING) {
						throw new CoreException(status);
					}

					ExecutionContext context = QvtLaunchUtil.createContext(configuration, new WriterLog(streamsProxy.getOutputWriter()), monitor);

					QvtLaunchUtil.doLaunch(qvtTransformation, configuration, context);

					return Status.OK_STATUS;
				}
				catch (QvtRuntimeException e) {
					// QVT runtime exception are legal QVT transformation level errors
					return BasicDiagnostic.toIStatus(e.getDiagnostic());
				}
				catch (Exception e) {
					IStatus actualStatus = new Status(IStatus.ERROR, QvtRuntimePlugin.ID,
							QvtRuntimePlugin.LAUNCH_ERROR_STATUS.getCode(), e.getMessage(),
							e.getMessage() == null ? e : null);

					IStatusHandler statusHandler = DebugPlugin.getDefault().getStatusHandler(QvtRuntimePlugin.LAUNCH_ERROR_STATUS);

					if(statusHandler != null) {
						try {
							statusHandler.handleStatus(actualStatus, configuration);
						} catch (CoreException coreExc) {
							actualStatus = coreExc.getStatus();
							QvtPlugin.getDefault().log(actualStatus);
						}
					}

					QvtPlugin.error(Messages.InMemoryQvtLaunchConfigurationDelegate_TransformationJobName, e);

					throw new CoreException(actualStatus);
				}
				finally {
					qvtTransformation.cleanup();
				}
			}
		};

		launch.addProcess(processJob);

		processJob.schedule();
	}


	// FIXME - do we need such an annoying generic check ?
	// Useless anyway without overriding
	//	org.eclipse.debug.core.model.LaunchConfigurationDelegate.getProjectsForProblemSearch(ILaunchConfiguration, String)
	//    @Override
	//	protected boolean existsProblems(IProject proj) throws CoreException {
	//        IMarker[] markers = proj.findMarkers(QVTOProjectPlugin.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
	//        for (int i = 0; i < markers.length; i++) {
	//            if (isLaunchProblem(markers[i])) {
	//                return true;
	//            }
	//        }
	//
	//        return false;
	//    }

}
