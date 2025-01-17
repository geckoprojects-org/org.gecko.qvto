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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.launch;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.TransformationRunner;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelExtentContents;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.common.launch.IQvtLaunchConstants;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData.TargetType;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.ModelContent;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.internal.qvt.oml.runtime.QvtRuntimePlugin;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.util.MiscUtil;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;
import org.eclipse.m2m.qvt.oml.ExecutionContext;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.util.Log;
import org.eclipse.m2m.qvt.oml.util.WriterLog;
import org.eclipse.osgi.util.NLS;


public class QvtLaunchUtil {

	private QvtLaunchUtil() {}

	public static ILaunchConfigurationType getInMemoryLaunchConfigurationType() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(InMemoryQvtLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE_ID);
		if (type == null) {
			throw new RuntimeException("No launch configuration for id " + InMemoryQvtLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE_ID); //$NON-NLS-1$
		}
		return type;
	}

	public static List<TargetUriData> getTargetUris(ILaunchConfiguration configuration) throws CoreException {
		int elemCount = configuration.getAttribute(IQvtLaunchConstants.ELEM_COUNT, 0);
		List<TargetUriData> targetUris = new ArrayList<TargetUriData>(elemCount);
		for (int i = 0; i < elemCount; ++i) {
			TargetUriData targetData = QvtLaunchUtil.getTargetUriData(configuration, i+1);
			targetUris.add(targetData);
		}
		return targetUris;
	}

	public static String getTransformationURI(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IQvtLaunchConstants.MODULE, (String) null);
	}

	public static String getTraceFileURI(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IQvtLaunchConstants.TRACE_FILE, (String) null);
	}

	public static boolean shouldGenerateTraceFile(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IQvtLaunchConstants.USE_TRACE_FILE, false);
	}

	public static boolean isIncrementalUpdate(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IQvtLaunchConstants.IS_INCREMENTAL_UPDATE, false);
	}

	public static TargetUriData getTargetUriData(ILaunchConfiguration configuration, int index) throws CoreException {
		TargetUriData.TargetType targetType = TargetUriData.TargetType.NEW_MODEL;
		try {
			targetType = TargetType.valueOf(configuration.getAttribute(getIndexedName(IQvtLaunchConstants.TARGET_TYPE, index), "NEW_MODEL")); //$NON-NLS-1$
		}
		catch (Exception e) {
			targetType = TargetUriData.TargetType.NEW_MODEL;
		}

		String uri = configuration.getAttribute(getIndexedName(IQvtLaunchConstants.TARGET_MODEL, index), ""); //$NON-NLS-1$
		String feature = configuration.getAttribute(getIndexedName(IQvtLaunchConstants.FEATURE_NAME, index), ""); //$NON-NLS-1$
		boolean clearContents = configuration.getAttribute(getIndexedName(IQvtLaunchConstants.CLEAR_CONTENTS, index), true);

		TargetUriData.ContentProvider contentProvider = null;
		if (configuration.getAttributes() != null) {
			contentProvider = (TargetUriData.ContentProvider) configuration.getAttributes().get(getIndexedName(IQvtLaunchConstants.CONTENT_PROVIDER, index));
		}

		return new TargetUriData(targetType, uri, feature, clearContents, contentProvider);
	}

	public static void saveTargetUriData(ILaunchConfigurationWorkingCopy configuration, List<TargetUriData> targetData) {
		int index = 1;
		for (TargetUriData targetUri : targetData) {
			QvtLaunchUtil.saveTargetUriData(configuration, targetUri, index);
			++index;
		}
	}

	public static void saveTargetUriData(ILaunchConfigurationWorkingCopy configuration, TargetUriData targetData, int index) {
		configuration.setAttribute(getIndexedName(IQvtLaunchConstants.TARGET_TYPE, index), targetData.getTargetType().toString());
		configuration.setAttribute(getIndexedName(IQvtLaunchConstants.TARGET_MODEL, index), targetData.getUriString());
		configuration.setAttribute(getIndexedName(IQvtLaunchConstants.FEATURE_NAME, index), targetData.getFeature());
		configuration.setAttribute(getIndexedName(IQvtLaunchConstants.CLEAR_CONTENTS, index), targetData.isClearContents());

		if (targetData.getContentProvider() != null) {
			try {
				Map<String, Object> attributes = new LinkedHashMap<String, Object>(configuration.getAttributes());
				attributes.put(getIndexedName(IQvtLaunchConstants.CONTENT_PROVIDER, index), targetData.getContentProvider());
				configuration.setAttributes(attributes);
			} catch (CoreException e) {
			}
		}
	}

	public static Map<String, Object> getConfigurationProperty(ILaunchConfiguration configuration) {
		return loadConfigurationProperties(configuration);
	}

	public static ExecutionContext createContext(ILaunchConfiguration configuration) {
		return createContext(configuration, Log.NULL_LOG, new NullProgressMonitor());
	}

	public static ExecutionContext createContext(ILaunchConfiguration configuration, Log log, IProgressMonitor monitor) {
		return createContext(getConfigurationProperty(configuration), log, monitor);
	}

	public static ExecutionContext createContext(Map<String, Object> configProps) {
		return createContext(configProps, Log.NULL_LOG);
	}

	public static ExecutionContext createContext(Map<String, Object> configProps, Log log) {
		return createContext(configProps, log, new NullProgressMonitor());
	}

	public static ExecutionContext createContext(Map<String, Object> configProps, Log log, IProgressMonitor monitor) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		if (configProps != null) {
			for (String name : configProps.keySet()) {
				context.setConfigProperty(name, configProps.get(name));
			}
		}

		context.setLog(log);
		context.setProgressMonitor(monitor);

		return context;
	}

	public static Map<String, Object> loadConfigurationProperties(ILaunchConfiguration configuration) {
		Map<String, Object> map;
		try {
			Map<String, String> configProps = configuration.getAttribute(IQvtLaunchConstants.CONFIGURATION_PROPERTIES, Collections.<String, String>emptyMap());
			map = new LinkedHashMap<String, Object>(configProps);
		} catch (CoreException e) {
			map = Collections.<String, Object>emptyMap();
			QvtRuntimePlugin.getDefault().getLog().log(MiscUtil.makeErrorStatus(e));
		}
		return map;
	}

	public static Map<String, Object> getConfigProperties(ExecutionContext context) {
		Map<String, Object> props = new HashMap<String, Object>();
		for(String name : context.getConfigPropertyNames()) {
			props.put(name, context.getConfigProperty(name));
		}
		return props;
	}



	private static String getIndexedName(String name, int index){
		if (index == 0) {
			return name;
		}
		return name + index;
	}

	public static void doLaunch(QvtTransformation transformation, ILaunchConfiguration configuration, ExecutionContext context) throws CoreException {
		List<TargetUriData> targetUris = getTargetUris(configuration);

		List<URI> paramUris = new ArrayList<URI>(targetUris.size());
		for(TargetUriData data : targetUris) {
			paramUris.add(data.getUri());
		}

		String traceFile = getTraceFileURI(configuration);
		doLaunch(transformation, paramUris, toUri(traceFile), context, shouldGenerateTraceFile(configuration), isIncrementalUpdate(configuration));
	}

	@Deprecated
	public static void doLaunch(QvtTransformation transformation, List<ModelContent> inObjs, Map<String, Object> configProps,
			List<ModelExtentContents> outExtents, List<EObject> outMainParams, List<Trace> outTraces, List<String> outConsole) throws MdaException {

		IStatus status = QvtValidator.validateTransformation(transformation, inObjs, null);
		if (status.getSeverity() > IStatus.WARNING) {
			throw new MdaException(status);
		}

		final StringWriter consoleLogger = new StringWriter();

		ExecutionContext context = createContext(configProps, new WriterLog(consoleLogger));

		org.eclipse.m2m.internal.qvt.oml.runtime.generator.TransformationRunner.In in =
				new org.eclipse.m2m.internal.qvt.oml.runtime.generator.TransformationRunner.In(
						inObjs.toArray(new ModelContent[inObjs.size()]), context);
		org.eclipse.m2m.internal.qvt.oml.runtime.generator.TransformationRunner.Out out = transformation.run(in);

		outExtents.addAll(out.getExtents());

		for (Object outValue : out.getOutParamValues()) {
			if (outValue instanceof EObject) {
				outMainParams.add((EObject) outValue);
			}
			else {
				outMainParams.add(null);
			}
		}

		if (out.getTrace() != null) {
			outTraces.add(out.getTrace());
		}
		outConsole.add(consoleLogger.getBuffer().toString());
	}

	public static void doLaunch(final QvtTransformation transf, List<URI> paramUris, URI traceUri, ExecutionContext context,
			boolean isSaveTrace, boolean isIncrementalUpdate) throws CoreException {

		TransformationRunner runner = new QvtTransformationRunner(transf, paramUris);

		runner.setTraceFile(traceUri);
		runner.setSaveTrace(isSaveTrace && traceUri != null);
		runner.setIncrementalUpdate(isIncrementalUpdate && traceUri != null);

		Diagnostic diag = runner.execute(context);

		IStatus status = BasicDiagnostic.toIStatus(diag);

		if (!status.isOK()) {

			Throwable e = diag.getException();

			// ensure stack trace access by propagating QvtRuntimeException
			if (e instanceof QvtRuntimeException) {
				throw (QvtRuntimeException) e;
			}

			throw new CoreException(status);
		}
	}

	private static URI toUri(String uriString) throws CoreException {
		if(uriString == null || uriString.length() == 0) {
			return null;
		}

		URI uri = EmfUtil.makeUri(uriString);
		if(uri == null) {
			throw new CoreException(new Status(IStatus.ERROR, QvtRuntimePlugin.ID, NLS.bind(Messages.QvtValidator_InvalidUri, uriString)));
		}

		return uri;
	}

	private static class QvtTransformationRunner extends TransformationRunner {
		public QvtTransformationRunner(QvtTransformation transf, List<URI> paramUris) {
			super(transf, paramUris);
		}
	}
}
