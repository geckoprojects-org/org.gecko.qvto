/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.common.launch.ShallowProcess;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData.TargetType;
import org.eclipse.m2m.internal.qvt.oml.emf.util.ModelContent;
import org.eclipse.m2m.internal.qvt.oml.emf.util.StatusUtil;
import org.eclipse.m2m.internal.qvt.oml.runtime.launch.QvtLaunchConfigurationDelegateBase;
import org.eclipse.m2m.internal.qvt.oml.runtime.launch.QvtValidator;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtInterpretedTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.TransformationUtil;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation.TransformationParameter;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtTransformation.TransformationParameter.DirectionKind;
import org.eclipse.osgi.util.NLS;

/**
 *  <p>
 *  All URIs used in transformation task (that are: transformation URI,
 *  transformation's parameter URI, trace URI) are resolved due to the following rules:
 *  
 *  <ul>
 *  <li>
 *  in case it's platform URI (URI.isPlatform() == true) -
 *      it is used as-is
 *  <li>
 *  in case it's non-relative file URI (URI.isRelative() == false) - 
 *      it's resolved against workspace root
 *  <li>
 *  in case it's relative file URI (URI.isRelative() == false) but starts with ["\", "/"] - 
 *      it's resolved against workspace root
 *  <li>
 *  in case it's relative file URI (URI.isRelative() == false) and does not start with ["\", "/"] - 
 *      it's resolved against script location
 *  </ul>
 *  
 *  <hr>
 *  <p>
 *  Ant task example:
 *  <br>
 *  
 *  &lt;project name="project" default="default" xmlns:qvto="http://www.eclipse.org/qvt/1.0.0/Operational"><br>
 *  &lt;target name="default"><br>
 *      &lt;qvto:transformation uri="platform:/resource/qvto/transforms/NewTransformation.qvto"><br>
 *  <br>
 *           &lt;inout<br>
 *               uri="platform:/resource/qvto/transforms/in.ecore"<br>
 *               outuri="platform:/resource/qvto/transforms/in_out.ecore"<br>
 *           /><br>
 *           &lt;out uri="platform:/resource/qvto/transforms/out.ecore#//cls"/><br>
 *  <br>
 *           &lt;trace uri="platform:/resource/qvto/transforms/NewTransformation.qvtotrace"/><br>
 *       &lt;/qvto:transformation><br>
 *  &lt;/target><br>
 *  &lt;/project><br>
 *
 */
public class QvtoAntTransformationTask extends Task {
	
	public static final String QVTO_ANTTASK_NAME = "qvto:transformation"; //$NON-NLS-1$
	
	public static interface ModelParameter {
	}
	
	/**
	 *  ModelParameter of [in] type for the transformation 
	 */
	public static class In implements ModelParameter {
	    
	    public In() {
	    }
	    
	    URI getURI(ProjectComponent project) {
	        return toUri(myUri, project);
	    }
	    
	    public void setUri(String uri) {
	        myUri = uri;
	    }
	    
	    private String myUri;
	}
	
	/**
	 *  ModelParameter of [inout] type for the transformation 
	 */
	public static class Inout implements ModelParameter {
	    
	    public Inout() {
	    }
	    
	    URI getURI(ProjectComponent project) {
	        return toUri(myUri, project);
	    }
	    
	    public void setUri(String uri) {
	        myUri = uri;
	    }
	    
	    URI getOutURI(ProjectComponent project) {
	        String outUriString = myOutUri != null && myOutUri.trim().length() > 0 ? myOutUri : myUri;
	        return toUri(outUriString, project);
	    }
	    
	    public void setOuturi(String uri) {
	    	myOutUri = uri;
	    }
	    
	    TargetUriData getTargetUriData(ProjectComponent project) {
	        return new TargetUriData(getOutURI(project).toString());
	    }
	    
	    private String myUri;
	    private String myOutUri;
	}
	
	/**
	 *  ModelParameter of [out] type for the transformation 
	 */
	public static class Out implements ModelParameter {
	    
	    public Out() {
	    }
	    
	    URI getURI(ProjectComponent project) {
	        return toUri(myUri, project);
	    }
	    
	    public void setUri(String uri) {
	        myUri = uri;
	    }
	    
	    public void addConfiguredFeature(Feature feature) {
	    	myFeature = feature;
	    }
	    
	    TargetUriData getTargetUriData(ProjectComponent project) {
	    	String feature = myFeature != null ? myFeature.getName() : null;
	        return new TargetUriData(
	        		feature != null && feature.trim().length() > 0 ? TargetType.EXISTING_CONTAINER : TargetType.NEW_MODEL,
	        		getURI(project).toString(),
	        		feature.trim(),
	        		Boolean.valueOf(myFeature.getClearContents())
	        		);
	    }
	    
	    private String myUri;
	    private Feature myFeature;
	}
	
	public static class Feature {
		
        public String getName() {
            return myName;
        }
        
        public void setName(String name) {
            myName = name;
        }
        
        public String getClearContents() {
            return myClearcontents;
        }
        
        public void setClearcontents(String clearcontents) {
        	myClearcontents = clearcontents;
        }
        
        private String myName;
        private String myClearcontents;
	}
	
	/**
	 *  Trace element - specifies a trace definition 
	 *  for the transformation 
	 */
	public static class Trace {
	    
	    public Trace() {
	    }
	    
	    public URI getURI(ProjectComponent project) {
	        return toUri(myUri, project);
	    }
	    
	    public void setUri(String uri) {
	        myUri = uri;
	    }
	    
	    private String myUri;
	}
	
	/**
	 *  Configuration property element - specifies a configuration property value 
	 *  for the transformation 
	 */
	public static class ConfigProperty {
	    
	    public ConfigProperty() {
	    }
	    
	    public String getName() {
	        return myName;
	    }
	    
	    public void setName(String name) {
	        myName = name;
	    }
	    
	    public String getValue() {
	        return myValue;
	    }
	    
	    public void setValue(String value) {
	        myValue = value;
	    }
	    
	    private String myName;
	    private String myValue;
	}

	public void setUri(String moduleUri) {
	    myModuleUri = moduleUri;
	}
	
	public void addConfiguredIn(In parameter) {
	   	myModelParameters.add(parameter);
	}
	
	public void addConfiguredInout(Inout parameter) {
	   	myModelParameters.add(parameter);
	}
	
	public void addConfiguredOut(Out parameter) {
	   	myModelParameters.add(parameter);
	}
	
	public void addConfiguredTrace(Trace trace) {
		myTrace = trace;
	}
	
	public void addConfiguredConfigProperty(ConfigProperty configProperty) {
		myConfigProperties.put(configProperty.getName(), configProperty.getValue());
	}
	
	
	/**
	 * Checks up parameters validity and launch transformation
	 */
	@Override
	public void execute() throws BuildException {
	
	    final QvtTransformation transformation = getTransformationObject();
	    
	    try {
	        ShallowProcess.IRunnable r = new ShallowProcess.IRunnable() {
	            public void run() throws Exception {
	            	List<ModelContent> inObjects = new ArrayList<ModelContent>();
	            	List<TargetUriData> targetData = new ArrayList<TargetUriData>();
	            	
	        		loadTransformationParams(transformation, inObjects, targetData);
	
	                IStatus status = QvtValidator.validateTransformation(transformation, inObjects);                    
	                if (status.getSeverity() > IStatus.WARNING) {
	                	throw new MdaException(status);
	                }      	
	        		
	        		QvtLaunchConfigurationDelegateBase.doLaunch(transformation,
	        				inObjects, targetData, getConfiguration(), getTraceUri(QvtoAntTransformationTask.this));
	        		
	        		transformation.cleanup();
	            }

	        };
	        
	        r = QvtLaunchConfigurationDelegateBase.getSafeRunnable(transformation, r);
	        r.run();
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	        throw new BuildException(StatusUtil.getExceptionMessages(e), e);
	    }
	
	    System.out.println(NLS.bind(Messages.TransformationExecuted, getModuleURI(this)));   
	}    
	
	/**
	 * @return a transformation to be executed
	 */
	private QvtTransformation getTransformationObject() {
        try {
			return new QvtInterpretedTransformation(TransformationUtil.getQvtModule(getModuleURI(this)));
		} catch (Exception e) {
            throw new BuildException(
                    NLS.bind(
                            Messages.AbstractApplyTransformationTask_File_not_found, 
                            getModuleURI(this)
                        ),
                    e
                    );
		}
	}

	private void loadTransformationParams(QvtTransformation transformation,
			List<ModelContent> inObjects, List<TargetUriData> targetData) throws MdaException {
		
		Iterator<ModelParameter> itrModelParam = myModelParameters.iterator();
		for (TransformationParameter transfParam : transformation.getParameters()) {
			if (!itrModelParam.hasNext()) {
	            throw new BuildException(NLS.bind(Messages.AbstractApplyTransformationTask_Required_attribute_is_not_specified,
	            		transfParam.getName()));
			}
			ModelParameter modelParam = itrModelParam.next();
			
			if (transfParam.getDirectionKind() == DirectionKind.IN) {
				if (false == modelParam instanceof In) {
    	            throw new BuildException(NLS.bind(Messages.ModelParameterTypeMismatch,
    	            		transfParam.getName(), DirectionKind.IN.name().toLowerCase()));
				}
				In inParam = (In) modelParam;
				
		        ModelContent inModel = transformation.loadInput(inParam.getURI(this));
		        inObjects.add(inModel);
			}
			if (transfParam.getDirectionKind() == DirectionKind.INOUT) {
				if (false == modelParam instanceof Inout) {
    	            throw new BuildException(NLS.bind(Messages.ModelParameterTypeMismatch,
    	            		transfParam.getName(), DirectionKind.INOUT.name().toLowerCase()));
				}
				Inout inoutParam = (Inout) modelParam;
				
		        ModelContent inModel = transformation.loadInput(inoutParam.getURI(this));
		        inObjects.add(inModel);

		        targetData.add(inoutParam.getTargetUriData(this));
			}
			if (transfParam.getDirectionKind() == DirectionKind.OUT) {
				if (false == modelParam instanceof Out) {
    	            throw new BuildException(NLS.bind(Messages.ModelParameterTypeMismatch,
    	            		transfParam.getName(), DirectionKind.OUT.name().toLowerCase()));
				}
				Out outParam = (Out) modelParam;

				targetData.add(outParam.getTargetUriData(this));
			}
		}
	}

	private URI getModuleURI(ProjectComponent project) {
	    return toUri(myModuleUri, project);
	}
	
	private String getTraceUri(ProjectComponent project) {
		if (myTrace == null) {
			return null;
		}
		return myTrace.getURI(project).toString();
	}
	
	private Map<String, Object> getConfiguration() {
	    return myConfigProperties;
	}
	
	private static URI toUri(String uriString, ProjectComponent project) throws BuildException {
	    try {
	    	URI uri = URI.createURI(uriString);
	    	if(uri == null) {
	    		throw new BuildException(NLS.bind(Messages.InvalidUriSpecified, uriString));
	    	}
	    	
			if (uri.isRelative() && !uriString.trim().startsWith(MSDOS_FS) && !uriString.trim().startsWith(UNIX_FS)) {
				URI baseUri = URI.createFileURI(project.getProject().getBaseDir().getAbsolutePath());
				uri = baseUri.appendSegments(uri.segments());
			}
	    	return uri;
	    }
	    catch (Exception e) {
	    	throw new BuildException(e);
	    }
	}
	
	    
	private String myModuleUri;
	private Trace myTrace;
	
	private final List<ModelParameter> myModelParameters = new ArrayList<ModelParameter>(3);
	private final Map<String, Object> myConfigProperties = new LinkedHashMap<String, Object>(3);
	
	//private static final String SYSTEM_FS = System.getProperty("file.separator"); //$NON-NLS-1$
	private static final String MSDOS_FS = "\\"; //$NON-NLS-1$
	private static final String UNIX_FS = "/"; //$NON-NLS-1$
	
}
