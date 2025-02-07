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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.qvt.osgi.api.ModelTransformator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.propertytypes.EventType;
import org.osgi.util.promise.PromiseFactory;

@Component(name = "Redirect", configurationPolicy = ConfigurationPolicy.REQUIRE)
@EventType(value = EObject.class)
public class RedirectEventHandler<T extends EObject> implements TypedEventHandler<T> {

	@Reference
	TypedEventBus bus;

	@Reference
	ModelTransformator transformation;
	
	private PromiseFactory promiseFactory;

	private CountDownLatch cLatch = new CountDownLatch(1);
	
	@Activate
	public void activate() {
		System.out.println("--- activate Redirect");
		promiseFactory = new PromiseFactory(Executors.newCachedThreadPool());
	}

	public boolean waitForEvent(int seconds) throws InterruptedException {
		return cLatch.await(seconds, TimeUnit.SECONDS);
	}
	
	@Override
	public void notify(String topic, T event) {
		promiseFactory.submit(() -> handleNotify(topic, event))
		.onFailure(e -> e.printStackTrace());
	}
	private Object handleNotify(String topic, T event) {
		cLatch.countDown();
		System.out.println("--- Topic: " + topic + " Event: " + event);
		EObject result = transformation.doTransformation(event);
		URI uri = EcoreUtil.getURI(result.eClass());
		String targetTopic = uri.toString().replace("http://", "").replace("https://", "").replace(".", "_")
				.replace("#", "").replace("//", "/");
		bus.deliver(targetTopic, result);
		return true;
	}

}