/*******************************************************************************
 * Copyright (c) 2009, 2021 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *     Christopher Gerking - bug 326871
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

class BundleModuleHandle extends ModuleHandle {

	private final String bundleId;
	private final List<String> usedPackages;		
	
	BundleModuleHandle(String bundleId, String className, String moduleName, List<String> usedPackages) {
		super(className, moduleName);
		
		if(bundleId == null || usedPackages == null) {
			throw new IllegalArgumentException();
		}

		this.bundleId = bundleId;
		this.usedPackages = usedPackages;
	}
	
	@Override
	public List<String> getUsedPackages() {
		List<String> packages = new ArrayList<String>(usedPackages);
		packages.addAll(super.getUsedPackages());
		return packages;
	}
	
	@Override
	public Class<?> getModuleJavaClass() throws ClassNotFoundException {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Bundle bundle = Arrays.stream(bundleContext.getBundles()).filter(b -> b.getSymbolicName().equals(bundleId)).findFirst().get();
//		Bundle bundle = Platform.getBundle(bundleId);
		if(bundle != null) {
			return bundle.loadClass(getJavaClassName());
		}
		return getClass().getClassLoader().loadClass(getJavaClassName());
	}
	
	@Override
	public String toString() {			
		return super.toString() + ", bundle: " + bundleId; //$NON-NLS-1$
	}				
}