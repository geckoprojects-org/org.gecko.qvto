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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.fennec.qvt.event.QvtEventConverter;
import org.eclipse.fennec.qvt.osgi.api.ModelTransformationConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * 
 * @author grune
 * @since Feb 3, 2025
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
class QvtEventConverterTest {

	@Test
	@WithFactoryConfiguration(name = "testPersonToAddress", factoryPid = ModelTransformationConstants.TRANSFORMATOR_COMPONENT_NAME, location = "?", //
			properties = @Property(key = ModelTransformationConstants.TEMPLATE_PATH, value = "org.eclipse.fennec.qvt.tests/PersonToAddressTransformation.qvto"))
	@WithFactoryConfiguration(name = "testPersonToPerson", factoryPid = ModelTransformationConstants.TRANSFORMATOR_COMPONENT_NAME, location = "?", //
	properties = @Property(key = ModelTransformationConstants.TEMPLATE_PATH, value = "org.eclipse.fennec.qvt.tests/PersonTransformation.qvto"))
	@WithFactoryConfiguration(name = "testPersonToPersonWithDeps", factoryPid = ModelTransformationConstants.TRANSFORMATOR_COMPONENT_NAME, location = "?", //
	properties = @Property(key = ModelTransformationConstants.TEMPLATE_PATH, value = "org.eclipse.fennec.qvt.tests/PersonTransformationWithDeps.qvto"))
	@WithFactoryConfiguration(name = "Address", factoryPid = "Address", location = "?", //
	properties = { @Property(key = "name", value = "MyAddress") })
	@WithFactoryConfiguration(name = "EObjectAddress", factoryPid = "EObjectAddress", location = "?", //
	properties = { @Property(key = "name", value = "MyAddress2") })
	void testConvert(@InjectService QvtEventConverter converter, @InjectService TypedEventBus bus, //
			@InjectService(filter = "(name=MyAddress)", cardinality = 0) ServiceAware<TypedEventHandler> awAddressHandler) throws InterruptedException {
		
		assertThat(converter).isNotNull();

		AddressEventHandler addressHandler = (AddressEventHandler) awAddressHandler.waitForService(500);
		
		assertThat(addressHandler).isNotNull();

		bus.deliver(ModelHelper.createPerson());
		
		assertThat(addressHandler.waitForEvent(5)).isTrue();
	}

}
