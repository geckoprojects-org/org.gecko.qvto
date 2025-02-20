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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.EObject;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.propertytypes.EventTopics;
import org.osgi.util.promise.PromiseFactory;

@Component(name="Address2",immediate = true, property = {"eClassUris=" + BasicPackage.eNS_URI + "//#Address"})
@EventTopics({"org/gecko/emf/osgi/example/model/basic/impl/PersonImpl","gecko_org/example/model/basic/Address"})
public class AddressEventHandler2 implements TypedEventHandler<EObject> {

	private PromiseFactory promiseFactory;
	private CountDownLatch cLatch = new CountDownLatch(1);

	@Activate
	public void activate() {
		System.out.println("--- activate Address" );
		promiseFactory = new PromiseFactory(Executors.newCachedThreadPool());
	}
	public boolean waitForEvent(int seconds) throws InterruptedException {
		return cLatch.await(seconds, TimeUnit.SECONDS);
	}
	@Override
	public void notify(String topic, EObject event) {
		promiseFactory.submit(() -> handleNotify(topic, event))
		.onFailure(e -> e.printStackTrace());
	}

	private Object handleNotify(String topic, EObject event) {
		cLatch.countDown();
		System.out.println("Address--- Topic: "+ topic + " Event: " + event);
		return true;
	}

}