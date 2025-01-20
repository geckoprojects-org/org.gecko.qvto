/**
 * <copyright>
 * Copyright (c) 2008, 2018 Open Canarias S.L. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Adolfo Sanchez-Barbudo Herrera - initial API and implementation
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.emf.common.util.ResourceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

public class ImperativeOCLPlugin implements ResourceLocator {

	/**
	 * The singleton instance of the plugin.
	 */
	public static final ImperativeOCLPlugin INSTANCE = new ImperativeOCLPlugin();
	
	private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("png","gif", "bmp","ico","jpg","jpeg", "tif","tiff");
	
	public ResourceLocator getPluginResourceLocator() {
		return this;
	}

	@Override
	public URL getBaseURL() {
		return FrameworkUtil.getBundle(getClass()).getEntry("/");
	}

	@Override
	public Object getImage(String key) {
		String path = getBaseURL() + "icons/" + key + extensionFor(key);
		try {
			URL url = new URL(path);
			InputStream inputStream = url.openStream();
			inputStream.close();
			return url;
		} catch (IOException exception) {
			throw new MissingResourceException("Missing properties: " + key, getClass().getName(),
					path);
		}
	}

	private static String extensionFor(String key) {
		String result = ".gif";
		int index = key.lastIndexOf('.');
		if (index != -1) {
			String extension = key.substring(index + 1).toLowerCase();
			if (IMAGE_EXTENSIONS.contains(extension)) {
				result = "";
			}
		}
		return result;
	}

	@Override
	public String getString(String key) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		String bundleLocalization = bundle.getHeaders().get(Constants.BUNDLE_LOCALIZATION);
		String propertiesPath = bundleLocalization != null ? bundleLocalization + ".properties" : "plugin.properties";
		String resourceName = getBaseURL().toString() + propertiesPath;
		try (InputStream inputStream = new URL(resourceName).openStream()) {
			return new PropertyResourceBundle(inputStream).getString(key);
		} catch (IOException ioException) {
			throw new MissingResourceException("Missing properties: " + resourceName, getClass().getName(),
					propertiesPath);
		}

	}

	@Override
	public String getString(String key, boolean translate) {
		return getString(key);
	}

	@Override
	public String getString(String key, Object[] substitutions) {
		return MessageFormat.format(getString(key), substitutions);
	}

	@Override
	public String getString(String key, Object[] substitutions, boolean translate) {
		return MessageFormat.format(getString(key), substitutions);
	}

}