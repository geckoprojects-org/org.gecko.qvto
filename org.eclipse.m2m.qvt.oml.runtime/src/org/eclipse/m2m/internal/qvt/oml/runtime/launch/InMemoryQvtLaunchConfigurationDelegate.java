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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.m2m.internal.qvt.oml.common.MDAConstants;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.common.launch.EmptyDebugTarget;
import org.eclipse.m2m.internal.qvt.oml.common.launch.IQvtLaunchConstants;
import org.eclipse.m2m.internal.qvt.oml.common.launch.ProcessJob;
import org.eclipse.m2m.internal.qvt.oml.common.launch.StreamsProxy;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtInterruptedExecutionException;
import org.eclipse.m2m.internal.qvt.oml.runtime.QvtRuntimePlugin;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtInterpretedTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.TransformationUtil;
import org.eclipse.m2m.internal.qvt.oml.runtime.util.MiscUtil;
import org.eclipse.m2m.qvt.oml.ExecutionContext;
import org.eclipse.m2m.qvt.oml.util.WriterLog;
import org.eclipse.osgi.util.NLS;


public class InMemoryQvtLaunchConfigurationDelegate extends QvtLaunchConfigurationDelegateBase {

	public static final String LAUNCH_CONFIGURATION_TYPE_ID = "org.eclipse.m2m.qvt.oml.InMemoryQvtTransformation"; //$NON-NLS-1$

	/**
	 * A handler for transformation job-done-status can be registered, taking argument as
	 * array of Object[] { Job, Runnable }, where <code>Runnable</code> element is optional
	 * and may execute a UI feedback on the job done event.
	 *
	 * @see IStatusHandler
	 */
	public static final IStatus statusJobDone = new Status(IStatus.INFO, QvtRuntimePlugin.ID, 200, "", null); //$NON-NLS-1$


	public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		String moduleUri = QvtLaunchUtil.getTransformationURI(configuration);
		final QvtTransformation qvtTransformation;
		try {
			qvtTransformation = new QvtInterpretedTransformation(TransformationUtil.getQvtModule(EmfUtil.makeUri(moduleUri)));
		} catch (MdaException e) {
			throw new IllegalArgumentException(NLS.bind(Messages.QvtBuilderLaunchTab_TransformationNotFound, moduleUri));
		}

		final Runnable doneAction = (Runnable) configuration.getAttributes().get(IQvtLaunchConstants.DONE_ACTION);
		final IProgressMonitor configMonitor = (IProgressMonitor) configuration.getAttributes().get(IQvtLaunchConstants.MONITOR);
		final IProgressMonitor actualMonitor = configMonitor != null ? configMonitor : monitor;

		final StreamsProxy streamsProxy = new StreamsProxy();

		ProcessJob processJob = new ProcessJob(Messages.InMemoryQvtLaunchConfigurationDelegate_TransformationJobName, launch, streamsProxy) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

				monitor.beginTask(NLS.bind(Messages.InMemoryQvtLaunchConfigurationDelegate_RunningTaskName, TransformationUtil.getTransformationFqn(qvtTransformation)),
						IProgressMonitor.UNKNOWN);
				try {
					IStatus status = QvtLaunchConfigurationDelegateBase.validate(qvtTransformation, configuration);
					if (status.getSeverity() > IStatus.WARNING) {
						throw new CoreException(status);
					}

					ExecutionContext context = QvtLaunchUtil.createContext(configuration, new WriterLog(streamsProxy.getOutputWriter()), actualMonitor);

					QvtLaunchUtil.doLaunch(qvtTransformation, configuration, context);

					IStatusHandler statusHandler = DebugPlugin.getDefault().getStatusHandler(statusJobDone);
					if(statusHandler != null) {
						Object[] handlerArgs = new Object[] { this, doneAction };
						Object resolution = statusHandler.handleStatus(statusJobDone, handlerArgs);
						assert resolution != null : "Invalid job done status resolution"; //$NON-NLS-1$
					}
				} catch (CoreException e) {
					throw e;
				} catch (Exception e) {
					String reason = e instanceof QvtInterruptedExecutionException ? "interrupted." : "failed."; //$NON-NLS-1$ //$NON-NLS-2$
					throw new CoreException(MiscUtil.makeErrorStatus("Transformation '" + TransformationUtil.getTransformationFqn(qvtTransformation) + "' " + reason, e)); //$NON-NLS-1$ //$NON-NLS-2$
				} finally {
					monitor.done();

					qvtTransformation.cleanup();
				}

				return Status.OK_STATUS;
			}
		};

		launch.addProcess(processJob);

		final EmptyDebugTarget debugTarget = new EmptyDebugTarget(launch, processJob, QvtRuntimePlugin.ID, MDAConstants.QVTO_LAUNCH_CONFIGURATION_NAME);

		launch.addDebugTarget(debugTarget);

		boolean isLaunchInBackground = true;
		try {
			isLaunchInBackground = !Boolean.FALSE.toString().equals(configuration.getAttribute(IQvtLaunchConstants.LAUNCH_IN_BACKGROUND, (String) null));
		} catch (Exception e) {
		}

		processJob.setUser(!isLaunchInBackground);

		processJob.schedule();
	}

	@Override
	protected IProject[] getProjectsForProblemSearch(ILaunchConfiguration configuration, String mode) throws CoreException {
		return new IProject[0];
	}
}
