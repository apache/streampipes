package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.implementations.stream.story.activitydetection.ActivityDetectionControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fzi.cep.sepa.implementations.stream.story.utils.UtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ ActivityDetectionControllerTest.class, UtilsTest.class })
public class AllTests {

}
