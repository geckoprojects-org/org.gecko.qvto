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
 *     Christopher Gerking - bug 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtilPlugin;
import org.eclipse.osgi.util.NLS;


/**
 * @author strizhkin
 */
public class EmfMetamodelDesc implements IMetamodelDesc {
	
    private final String myId;
    
    private final EPackage.Descriptor myPackageDescriptor;
    
    private EPackage myPackage;
    
    private Diagnostic status = Diagnostic.OK_INSTANCE;
	
	
	public EmfMetamodelDesc(EPackage.Descriptor packageDescriptor, String id) {
		if (packageDescriptor == null) {
			throw new IllegalArgumentException();
		}
		myPackageDescriptor = packageDescriptor;
		myId = id;
	}
		
	public EmfMetamodelDesc(EPackage pack, String nsURI) {
		if (pack == null) {
			throw new IllegalArgumentException();
		}
		myPackageDescriptor = null;
		myPackage = pack;
		myId = nsURI;
	}
	
	public String getId() {
		return myId;
	}
	
    public EPackage getModel() {
    	if (myPackage == null) {
    		try {
    			myPackage = myPackageDescriptor.getEPackage();
    		}
    		catch (Throwable ex) {
    			String errMessage = NLS.bind(Messages.MetamodelRegistry_LoadError, getId(), ex.getClass().getName());    			
    			//this.status = new Status(IStatus.ERROR, EmfUtilPlugin.ID, errMessage, ex);
    			this.status = EmfUtilPlugin.createErrorDiagnostic(errMessage, ex);
    			// create empty package representation
    			myPackage = EcoreFactory.eINSTANCE.createEPackage();
    			myPackage.setNsURI(getId());
    			myPackage.setName(""); //$NON-NLS-1$
    			// confine in a resource
    			new XMIResourceImpl(URI.createURI("http://unresolvedEPackage")).getContents().add(myPackage); //$NON-NLS-1$
    		}
    	}
        return myPackage;
    }
    
    public Diagnostic getLoadStatus() {
    	if(myPackage == null) {
    		// cause metamodel packages to load
    		getModel();
    	}
		return status;
	}
    
    public boolean isLoaded() {
    	return myPackage != null;
	}
    
    @Override
	public String toString() {
    	return getId();
    }    
}
