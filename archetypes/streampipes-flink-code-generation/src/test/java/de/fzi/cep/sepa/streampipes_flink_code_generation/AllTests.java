package de.fzi.cep.sepa.streampipes_flink_code_generation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControllerGeneratorTest.class, MainTest.class, UtilsTest.class, InitGeneratorTest.class })
public class AllTests {

}
