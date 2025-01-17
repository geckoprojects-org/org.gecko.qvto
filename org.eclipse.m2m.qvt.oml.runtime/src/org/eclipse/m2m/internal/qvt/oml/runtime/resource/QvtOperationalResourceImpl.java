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

package org.eclipse.m2m.internal.qvt.oml.runtime.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.m2m.internal.qvt.oml.QvtMessage;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.common.MdaException;
import org.eclipse.m2m.internal.qvt.oml.compiler.BlackboxUnitResolver;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTEvaluationOptions;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.QvtModule;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.TransformationUtil;
import org.eclipse.m2m.internal.qvt.oml.runtime.util.Messages;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author sboyko
 *
 */
public class QvtOperationalResourceImpl extends XMIResourceImpl {

    public QvtOperationalResourceImpl(URI uri) {
    	super(uri);
    }
	
    @Override
    public void load(Map<?, ?> options) throws IOException {
    	if (!isLoaded()) {
    		isLoading = true;

			if (errors != null) {
				errors.clear();
			}
			if (warnings != null) {
				warnings.clear();
			}
    		
            try {
            	URI normalizedUri = getURIConverter().normalize(getURI());
            	EPackage.Registry packageRegistry = getResourceSet() != null ? getResourceSet().getPackageRegistry() : EPackage.Registry.INSTANCE;
            	QvtModule qvtModule = TransformationUtil.getQvtModule(normalizedUri, packageRegistry);

            	QvtCompilerOptions qvtOptions = new QvtCompilerOptions();
            	qvtOptions.setModuleWithErrorAllowed(true);
            	qvtModule.setQvtCompilerOptions(qvtOptions);
            	
            	CompiledUnit unit = qvtModule.getUnit();
				fillCompilationDiagnostic(unit, normalizedUri);

				if (unit.getModules().isEmpty()) {
    				throw new IOException(NLS.bind(Messages.QvtResource_moduleCompilationErrors, 
    						normalizedUri, unit.getProblems()));
    			}

        		getContents().addAll(unit.getModules());
    			
    			if (getResourceSet() != null) {
	    			HashSet<CompiledUnit> allUnits = new HashSet<CompiledUnit>();		
	    			QvtOperationalParserUtil.collectAllImports(unit, allUnits);
	    			for (CompiledUnit nextUnit : allUnits) {
	    				URI nextUri = nextUnit.getURI();
	    				if (BlackboxUnitResolver.isBlackboxUnitURI(nextUri)) {
	    					continue;
	    				}
	    				Resource resource = getResourceSet().getResource(nextUri, false);
	    				if (resource == null) {
	    					getResourceSet().getResources().add(confineInResource(nextUnit));
	    				}
	    			}
    			}
            }
            catch (MdaException e) {
				throw new IOWrappedException(e);
			}
            finally {
				isLoading = false;
				
	    		Notification notification = setLoaded(true);
				if (notification != null) {
					eNotify(notification);
				}
				
				setModified(false);
            }
    	}
    }
    
	private static Resource confineInResource(CompiledUnit unit) {
		QvtOperationalResourceImpl rs = new QvtOperationalResourceImpl(unit.getURI());
		rs.getContents().addAll(unit.getModules());
		
		rs.fillCompilationDiagnostic(unit, unit.getURI());

		rs.setLoaded(true);
		rs.setModified(false);
		
		return rs;
	}

	private void fillCompilationDiagnostic(CompiledUnit unit, URI uri) {
    	warnings = getWarnings();
		for (QvtMessage msg : unit.getWarnings()) {
			warnings.add(new Diagnostic(msg.getMessage(), uri.toString(), msg.getLineNum()));
		}
		for (QvtMessage msg : unit.getErrors()) {
			warnings.add(new QvtCompilationErrorException(msg, uri.toString(), msg.getLineNum()));
		}
    }

	@Override
    public void doLoad(InputStream inputStream, Map<?, ?> options)
    		throws IOException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void doLoad(InputSource inputSource, Map<?, ?> options) throws IOException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void doLoad(Node node, Map<?, ?> options) throws IOException {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public void save(Map<?, ?> options) throws IOException {
    	if (!isUnparseEnabled(options)) {
    		return;
    	}
    	super.save(options);
    }
    
    @Override
    public void doSave(OutputStream outputStream, Map<?,?> options) throws IOException {
		this.doSave(new OutputStreamWriter(outputStream), options);
    }
    
	@Override
	public void doSave(Writer writer, Map<?, ?> options) throws IOException
	{
		UnparsingQVTOVisitor unparsingVisitor = new UnparsingQVTOVisitor();
		
		EList<EObject> contents = this.getContents();
		EList<Module> modules = new BasicEList<Module>();
		for (EObject eObject : contents)
		{
			Module module = (Module) eObject;
			modules.add(module);
		}
		unparsingVisitor.unparseModules(modules);
		
		for (String string : unparsingVisitor.getLines())
		{
			if ( string != null )
			{
				writer.write(string);
				writer.write(System.getProperty("line.separator")); //$NON-NLS-1$
			}
		}
		
		writer.flush();
	}
    
    @Override
    public boolean isModified() {
    	return false;
    }
    
    @Override
    public boolean isTrackingModification() {
    	return false;
    }
    
	/**
	 * Just a marker for diagnostic notification messages
	 */
	private static class Diagnostic implements Resource.Diagnostic {
		private final String myMessage;
		private final String myLocation;
		private final int myLine;

		Diagnostic(String message, String  location, int line) {
			myMessage = message;
			myLocation = location;
			myLine = line;
		}

		public String getMessage() {
			return myMessage;
		}		

		public String getLocation() {
			return myLocation;
		}

		public int getColumn() {
			return 0;
		}

		public int getLine() {
			return myLine;
		}
	}
	
	private boolean isUnparseEnabled(Map<?, ?> options) {
		return options.get(QVTEvaluationOptions.FLAG_QVTO_UNPARSE_ENABLED) != Boolean.FALSE;
	}

}
