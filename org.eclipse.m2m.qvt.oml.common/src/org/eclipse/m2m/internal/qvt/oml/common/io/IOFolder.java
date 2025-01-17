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
package org.eclipse.m2m.internal.qvt.oml.common.io;

import java.io.File;

public class IOFolder extends IOResource implements CFolder {
    public IOFolder(String folder) {
        this(new File(folder));
    }
    
	public IOFolder(File folder) {
		super(folder);
		if(folder.exists() && !folder.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + folder); //$NON-NLS-1$
		}
	}
	
	public CFile getFile(String name) {
		return new IOFile(new File(myFile, name));
	}

}
