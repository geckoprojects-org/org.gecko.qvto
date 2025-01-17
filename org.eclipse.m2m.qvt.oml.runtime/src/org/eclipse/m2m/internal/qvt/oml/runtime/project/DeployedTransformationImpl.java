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
 *     Christopher Gerking - bug 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.common.project.DeployedTransformation;
import org.eclipse.m2m.internal.qvt.oml.compiler.ResolverUtils;

public class DeployedTransformationImpl implements DeployedTransformation {
   	
	public DeployedTransformationImpl(String namespace, String id, String file) {
        myNamespace = namespace;
        myId = id;
        this.transformationFilePath = file != null ? new Path(file) :
        	new Path(ResolverUtils.toNamespaceRelativeUnitFilePath(id));
    }
	    
    public String getId() {
        return myId;
    }

    public URI getUri() {
    	return URI.createPlatformPluginURI(myNamespace, false).appendSegments(transformationFilePath.segments());
    }
    
    @Override
	public String toString() {
    	return myId;
    }
    
    @Override
	public boolean equals(Object obj) {
        if (obj instanceof DeployedTransformationImpl == false) {
            return false;
        }
        DeployedTransformationImpl trans = (DeployedTransformationImpl) obj;
        return trans.getId().equals(getId());
    }

    @Override
	public int hashCode() {
        return myId.hashCode();
    }
    
    private final String myNamespace;

    private final String myId;
    
    private IPath transformationFilePath;
}
