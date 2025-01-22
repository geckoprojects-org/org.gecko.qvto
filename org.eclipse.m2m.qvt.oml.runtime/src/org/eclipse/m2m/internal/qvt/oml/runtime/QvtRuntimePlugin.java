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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The main plugin class to be used in the desktop.
 */
//public class QvtRuntimePlugin extends Plugin {
public class QvtRuntimePlugin {
	
    public static final String ID = "org.eclipse.m2m.qvt.oml.runtime"; //$NON-NLS-1$
    
    public static final IStatus LAUNCH_ERROR_STATUS = new Status(IStatus.ERROR, QvtRuntimePlugin.ID, 210, "Launch configuration error", null); //$NON-NLS-1$	
    
	
	//The shared instance.
//    private static QvtRuntimePlugin plugin;
	private static QvtRuntimePlugin plugin = new QvtRuntimePlugin();
	
//	/**
//	 * The constructor.
//	 */
//	public QvtRuntimePlugin() {
//		super();
//		plugin = this;
//	}

//	/**
//	 * This method is called upon plug-in activation
//	 */
//	@Override
//	public void start(BundleContext context) throws Exception {
//		super.start(context);
//
//        ResourcesPlugin.getWorkspace().addResourceChangeListener(new DeleteBuilderMarkersListener(), IResourceChangeEvent.PRE_BUILD);
//	}
//
//	/**
//	 * This method is called when the plug-in is stopped
//	 */
//	@Override
//	public void stop(BundleContext context) throws Exception {
//		super.stop(context);
//		plugin = null;
//	}

	/**
	 * Returns the shared instance.
	 */
	public static QvtRuntimePlugin getDefault() {
		return plugin;
	}
        
//    public static void log(IStatus status) {
//    	Plugin debugPlugin = getDefault();
//		if(debugPlugin != null) {
//    		debugPlugin.getLog().log(status);
//    	}
//    }

//    public static void log(Throwable e) {
//        log(new Status(IStatus.ERROR, ID, "Exception caught", e)); //$NON-NLS-1$
//    }
   
	public static IStatus createStatus(int severity, String message, Throwable throwable) {
		return new Status(severity, ID, message, throwable);
	}
	
	public static IStatus createStatus(int severity, String message) {
		return createStatus(severity, message, null);
	}
}
