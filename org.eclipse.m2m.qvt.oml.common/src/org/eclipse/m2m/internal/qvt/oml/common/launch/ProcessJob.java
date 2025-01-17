/*******************************************************************************
 * Copyright (c) 2018, 2019 Christopher Gerking and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation, bug 537609
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.common.launch;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.m2m.internal.qvt.oml.common.CommonPlugin;
import org.eclipse.m2m.internal.qvt.oml.common.Messages;
import org.eclipse.osgi.util.NLS;

public abstract class ProcessJob extends WorkspaceJob implements IProcess {

	private ILaunch launch;
	private IStreamsProxy proxy;

	public ProcessJob(String name, ILaunch launch, IStreamsProxy proxy) {
		super(name);

		this.launch = launch;
		this.proxy = proxy;

		addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (DebugPlugin.getDefault() != null) {
            		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] {new DebugEvent(ProcessJob.this, DebugEvent.TERMINATE)});
            	}
			}
		});
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	public boolean isTerminated() {
		return getResult() != null;
	}

	public void terminate() {
		cancel();
	}

	public String getLabel() {
		String transformationURI = null;
		ILaunchConfiguration configuration = launch.getLaunchConfiguration();
		if(configuration != null) {
			try {
				transformationURI = configuration.getAttribute(IQvtLaunchConstants.MODULE, (String) null);
			} catch (CoreException e) {
				CommonPlugin.log(e.getStatus());
			}
		}

		return transformationURI != null
				? NLS.bind(Messages.IProcess_LabelTransform, transformationURI)
						: Messages.IProcess_Label;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public IStreamsProxy getStreamsProxy() {
		return proxy;
	}

	public void setAttribute(String key, String value) {}

	public String getAttribute(String key) {
		return null;
	}

	public int getExitValue() throws DebugException {
		if(!isTerminated()) {
            throw new DebugException(new Status(IStatus.ERROR, CommonPlugin.ID, 1, Messages.IProcess_InvalidState, null));
        }

		return getResult().getSeverity();
	}
}
