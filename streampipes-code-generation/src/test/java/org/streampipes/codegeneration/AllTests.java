package org.streampipes.codegeneration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.streampipes.codegeneration.flink.ConfigGeneratorTest;
import org.streampipes.codegeneration.flink.InitGeneratorTest;
import org.streampipes.codegeneration.flink.XmlGeneratorTest;
import org.streampipes.codegeneration.flink.sec.FlinkSecControllerGeneratorTest;
import org.streampipes.codegeneration.flink.sec.FlinkSecProgramGeneratorTest;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGeneratorTest;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaProgramGeneratorTest;
import org.streampipes.codegeneration.flink.sepa.ImplementationGeneratorTest;
import org.streampipes.codegeneration.flink.sepa.ParametersGeneratorTest;
import org.streampipes.codegeneration.utils.DirectoryBuilderTest;
import org.streampipes.codegeneration.utils.UtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ FlinkSepaControllerGeneratorTest.class, UtilsTest.class, InitGeneratorTest.class,
		XmlGeneratorTest.class, FlinkSepaProgramGeneratorTest.class, ImplementationGeneratorTest.class,
		ParametersGeneratorTest.class, ConfigGeneratorTest.class, DirectoryBuilderTest.class,
		FlinkSecControllerGeneratorTest.class, FlinkSecProgramGeneratorTest.class})
public class AllTests {

}
