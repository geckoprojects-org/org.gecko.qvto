/*******************************************************************************
 * Copyright (c) 2009, 2018 Borland Software Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Ed Willink - bug 540971
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.common.MDAConstants;
import org.eclipse.m2m.internal.qvt.oml.compiler.BlackboxUnitResolver;
import org.eclipse.m2m.internal.qvt.oml.compiler.DelegatingUnitResolver;
import org.eclipse.m2m.internal.qvt.oml.compiler.ResolverUtils;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitContents;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitProxy;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolver;

/* obsolete class re-instated to avoid GMF Tooling breakage - see Bug 540971 */
/* @deprecated use PlatformPluginUnitResolver  */
@Deprecated
public class BundleUnitResolver extends DelegatingUnitResolver {

	private final class BundleUnit extends UnitProxy {

		private final URL url;

		private BundleUnit(String namespace, String unitName, URL unitURL) {
			super(namespace, unitName, createUnitURI(unitURL));
			this.url = unitURL;
		}

		@Override
		public int getContentType() {
			return UnitProxy.TYPE_CST_STREAM;
		}

		@Override
		public UnitContents getContents() throws IOException {
			return new UnitContents.CSTContents() {
				public Reader getContents() throws IOException {
					URLConnection connection = url.openConnection();

					String charset = connection.getContentEncoding();
					if(charset == null) {
						charset = "UTF-8"; //$NON-NLS-1$
					}

					return new InputStreamReader(connection.getInputStream(), charset);
				}
			};
		}

		@Override
		public UnitResolver getResolver() {
			return BundleUnitResolver.this;
		}

	}


	private List<URL> fBaseURLs;

	public BundleUnitResolver(List<URL> baseURL) {
		if(baseURL == null || baseURL.contains(null)) {
			throw new IllegalArgumentException();
		}

		fBaseURLs = new ArrayList<URL>(baseURL);
	}

	@Override
	protected UnitProxy doResolveUnit(String qualifiedName) {
		for (URL baseURL : fBaseURLs) {
			UnitProxy unit = doResolveUnit(baseURL, qualifiedName);
			if(unit != null) {
				return unit;
			}
		}

		return null;
	}

	private UnitProxy doResolveUnit(URL baseURL, String qualifiedName) {
		String pathStr = qualifiedName.replace('.', '/');
		IPath path = new Path(pathStr).addFileExtension(MDAConstants.QVTO_FILE_EXTENSION);
		URL url;
		try {
			url = new URL(baseURL, path.toString());
		} catch (MalformedURLException e1) {
			return null;
		}

		InputStream is = null;
		try {
			is = url.openStream();

			String name = path.lastSegment();
			String namespace = null;
			if(path.segmentCount() > 1) {
				IPath nameSpacePath = path.removeLastSegments(1);
				namespace = ResolverUtils.getNamespace(nameSpacePath);
			}

			return new BundleUnit(namespace, name, url);

		} catch (IOException e) {
			return null;
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	private static URI createUnitURI(URL url) {
		// TODO - we might not necessarily be passed a platform URL
		// to do adjustments to produce a valid URI
		return URI.createURI(url.toString());
	}

	public static UnitResolver createResolver(List<URL> bundleBaseURLs, boolean includeBlackboxUnits) {
		BundleUnitResolver bundleUnitResolver = new BundleUnitResolver(bundleBaseURLs);
		if(includeBlackboxUnits) {
			bundleUnitResolver.setParent(BlackboxUnitResolver.DEFAULT);
		}

		return bundleUnitResolver;
	}
}
