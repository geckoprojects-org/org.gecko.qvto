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
package org.eclipse.m2m.internal.qvt.oml.common.launch;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

@Deprecated
public abstract class BaseProcess extends PlatformObject implements IProcess {

	public static interface IRunnable {

		public void run() throws Exception;

	}

	public void setStreamsProxy(IStreamsProxy streamsProxy) {
		myStreamsProxy = streamsProxy;
	}

	public IStreamsProxy getStreamsProxy() {
		return myStreamsProxy;
	}

	private IStreamsProxy myStreamsProxy;

}
