/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.qvt.event.tests;

import org.gecko.emf.osgi.example.model.basic.Address;
import org.gecko.emf.osgi.example.model.basic.BasicFactory;
import org.gecko.emf.osgi.example.model.basic.GenderType;
import org.gecko.emf.osgi.example.model.basic.Person;

/**
 */
class ModelHelper {

	private ModelHelper() {
	}

	static Person createPerson() {
		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Hans");
		person.setLastName("Wurst");
		person.setGender(GenderType.MALE);
		Address address = BasicFactory.eINSTANCE.createAddress();
		address.setCity("Jena");
		address.setStreet("Am Felsenkeller");
		address.setZip("07745");
		person.setAddress(address);
		return person;
	}
}
