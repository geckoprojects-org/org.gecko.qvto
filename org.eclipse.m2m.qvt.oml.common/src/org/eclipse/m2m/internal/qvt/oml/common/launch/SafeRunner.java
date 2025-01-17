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

import org.eclipse.m2m.internal.qvt.oml.common.launch.BaseProcess.IRunnable;

@Deprecated
public class SafeRunner {
	public static interface IRunner {
		public void run(BaseProcess.IRunnable r) throws Exception;
	}

	public static BaseProcess.IRunnable getSafeRunnable(final BaseProcess.IRunnable r) {
		final IRunner runner = SameThreadRunner.INSTANCE;
		return new BaseProcess.IRunnable() {
			public void run() throws Exception {
				runner.run(r);
			}
		};
	}

	static class SameThreadRunner implements IRunner {
		public void run(IRunnable r) throws Exception {
			r.run();
		}

		static IRunner INSTANCE = new SameThreadRunner();
	}
}