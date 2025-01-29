package org.eclipse.fennec.qvt.osgi.tests.bbox;

import org.eclipse.fennec.qvt.osgi.annotations.QvtBlackbox;
import org.eclipse.fennec.qvt.osgi.annotations.TemplatePath;
import org.eclipse.fennec.qvt.osgi.annotations.TransformatorId;
import org.eclipse.fennec.qvt.osgi.annotations.UnitQualifiedName;
import org.eclipse.m2m.qvt.oml.blackbox.java.Module;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.gecko.emf.osgi.example.model.basic.Person;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.SatisfyingConditionTarget;

/**
 * <p>QVT Blackbox for the sdg tlc communication state.</p>
 * <p>Copyright (c) SWARCO TRAFFIC SYSTEMS GMBH 2017</p>
 *
 * @author   Mark Hoffmann
 * @version  10.11.2017
 */
@Component(service = BlackboxComponentTest.class)
@SatisfyingConditionTarget("(osgi.condition.id=test)")
@QvtBlackbox
@TemplatePath("org.eclipse.fennec.qvt.tests/PersonTransformationWithBlackboxComponentRegistration.qvto")
@TransformatorId("testTrafo")
@UnitQualifiedName("bla.blub.BlackboxTest")
@Module(packageURIs={BasicPackage.eNS_URI})
public class BlackboxComponentTest {

	@Operation(contextual=true)
	public static String getModifiedLastName(Person self) {
		return self.getLastName() + "BlackBox";
	}

}