package de.fzi.cep.sepa.streampipes.codegeneration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fzi.cep.sepa.streampipes.codegeneration.flink.ConfigGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.InitGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.XmlGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sec.FlinkSecControllerGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sec.FlinkSecProgramGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaProgramGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ImplementationGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ParametersGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.DirectoryBuilderTest;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.UtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ FlinkSepaControllerGeneratorTest.class, UtilsTest.class, InitGeneratorTest.class,
		XmlGeneratorTest.class, FlinkSepaProgramGeneratorTest.class, ImplementationGeneratorTest.class,
		ParametersGeneratorTest.class, ConfigGeneratorTest.class, DirectoryBuilderTest.class,
		FlinkSecControllerGeneratorTest.class, FlinkSecProgramGeneratorTest.class})
public class AllTests {

}
