/*******************************************************************************
 * Copyright (c) 2006, 2011, 2018 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   Axel Uhl (SAP AG) - Bug 342644
 *******************************************************************************/
package org.eclipse.ocl.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.internal.evaluation.NumberUtil;

/**
 * Certain generic utility operations on objects.
 * 
 * @author Christian W. Damus (cdamus)
 */
public class ObjectUtil {

	/**
     * Computes the equivalence of two objects, accounting for primitive numeric
     * values that OCL considers equal but Java does not.  This is also safe
     * when either value is <code>null</code>.
     * 
     * @param anObject an object or <code>null</code>
     * @param anotherObject another object or <code>null</code>
     * 
     * @return whether they are equivalent as far as OCL is concerned
     */
	public static boolean equal(Object anObject, Object anotherObject) {
		// if either value is undefined, the result is true just if both are
		// undefined and false otherwise.
		if (anObject == null || anotherObject == null) {
            return anObject == anotherObject;
        }

		// primitive types
		if (isPrimitive(anObject) || isPrimitive(anotherObject)) {
		    if (anObject instanceof Integer) {
		        anObject = NumberUtil.higherPrecisionNumber((Integer) anObject);
		    }
            if (anotherObject instanceof Integer) {
                anotherObject = NumberUtil.higherPrecisionNumber((Integer) anotherObject);
            }
		    
			if (anObject instanceof Long && anotherObject instanceof Long) {
                return ((Long) anObject).longValue() == ((Long) anotherObject).longValue();
            } else if (anObject instanceof Long && anotherObject instanceof Double) {
                return ((Long) anObject).doubleValue() == ((Double) anotherObject).doubleValue();
            } else if (anObject instanceof Double && anotherObject instanceof Long) {
                return ((Double) anObject).doubleValue() == ((Long) anotherObject).doubleValue();
            } else if (anObject instanceof Double && anotherObject instanceof Double) {
                return ((Double) anObject).doubleValue() == ((Double) anotherObject).doubleValue();
            } else if (anObject instanceof String && anotherObject instanceof String) {
                return anObject.equals(anotherObject);
            } else if (anObject instanceof Boolean && anotherObject instanceof Boolean) {
                return ((Boolean) anObject).booleanValue() == ((Boolean) anotherObject).booleanValue();
            };

			// if the types are incompatible the result is false
			return false;
		}

		if ((anObject instanceof EEnumLiteral) && (anotherObject instanceof EEnumLiteral)) {
			return anObject == anotherObject;
		} else if ((anObject instanceof EEnumLiteral) && (anotherObject instanceof Enumerator)) {
			return ((EEnumLiteral) anObject).getInstance() == anotherObject;
		} else if ((anotherObject instanceof EEnumLiteral) && (anObject instanceof Enumerator)) {
			return ((EEnumLiteral) anotherObject).getInstance() == anObject;
		} else if ((anObject instanceof LinkedHashSet<?> && !(anotherObject instanceof LinkedHashSet<?>)) ||
				(!(anObject instanceof LinkedHashSet<?>) && anotherObject instanceof LinkedHashSet<?>)) {
			// a regular Java equals comparison would consider LinkedHashSets and Sets
			// with equals contents equals. However, according to OCL 2.3 (OMG 10-11-42) section 11.7.1,
			// two collections need to be of the same kind to be considered equal.
			return false;
		}
		if (anObject instanceof LinkedHashSet<?>) {
			// ...then so is anotherObject due to he above test.
			// LinkedHashSet.equals doesn't consider ordering as it
			// should for an OrderedSet implementation.
			return orderedSetsEqual((LinkedHashSet<?>) anObject, (LinkedHashSet<?>) anotherObject);
		}
		return anObject.equals(anotherObject);
	}
	
    private static boolean orderedSetsEqual(LinkedHashSet<?> anObject,
			LinkedHashSet<?> anotherObject) {
		if (anObject == anotherObject) {
			return true;
		}
		if (anObject.size() != anotherObject.size()) {
			return false;
		}
		// inv: sizes are equal
		if (anObject.isEmpty()) {
			// then so is anotherObject because sizes are equal
			return true;
		}
		Iterator<?> anObjectIter = anObject.iterator();
		Iterator<?> anotherObjectIter = anotherObject.iterator();
		while (anObjectIter.hasNext()) {
			if (!equal(anObjectIter.next(), anotherObjectIter.next())) {
				return false;
			}
		}
		return true;
	}

	/**
     * Computes hash of an object, accounting for the similar
     * hashing of primitive numeric values that OCL considers equal but Java
     * does not.  It is also safe with <code>null</code> values.
     * 
     * @param anObject an object or <code>null</code>
     * 
     * @return its OCL hash
     */
	public static int hashCode(Object anObject) {
		if (anObject == null) {
			return 0;
		}

		if (isPrimitive(anObject)) {
			// equal double and integer should hash the same 
			if (anObject instanceof Integer) {
                return 37 * ((Integer) anObject).intValue();
            } else if (anObject instanceof Long) {
                return 37 * ((Long) anObject).intValue();
            } else if (anObject instanceof Double) {
                return 37 * ((Double) anObject).intValue();
            } else if (anObject instanceof String) {
                return anObject.hashCode();
            } else if (anObject instanceof Boolean) {
                return anObject.hashCode();
            }

			// shouldn't get here (there are no other OCL primitives)
			return 0;
		}

		if (anObject instanceof EEnumLiteral) {
			return ((EEnumLiteral) anObject).getInstance().hashCode();
		}
		
		return anObject.hashCode();
	}

    /**
     * Queries whether the specified object represents an OCL primitive value.
     * 
     * @param o an object
     * @return whether it is an OCL primitive value
     */
	public static boolean isPrimitive(Object o) {
		return o instanceof Integer || o instanceof Long || o instanceof String
			|| o instanceof Boolean || o instanceof Double;
	}

	/**
	 * Disposes of the specified <tt>object</tt>.  If, in particular, it is
	 * an {@link EObject}, then it and all of its contents will have their
	 * adapter-lists cleared.
	 * 
	 * @param object an object to dispose
	 * 
	 * @since 1.2
	 */
	public static void dispose(Object object) {
	    if (object instanceof EObject) {
	        EObject eObject = (EObject) object;
	        
            eObject.eAdapters().clear();
            for (Iterator<EObject> iter = EcoreUtil.getAllContents(eObject,
                false); iter.hasNext();) {
                iter.next().eAdapters().clear();
            }
	    } else if (object instanceof Collection<?>) {
	        for (Object next : ((Collection<?>) object)) {
	            dispose(next);
	        }
	    }
	}
}
