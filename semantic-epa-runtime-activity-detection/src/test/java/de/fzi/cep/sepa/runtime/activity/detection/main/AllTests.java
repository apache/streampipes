package de.fzi.cep.sepa.runtime.activity.detection.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fzi.cep.sepa.runtime.activity.detection.utils.UtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ ActivityDetectionControllerTest.class, UtilsTest.class })
public class AllTests {

}
