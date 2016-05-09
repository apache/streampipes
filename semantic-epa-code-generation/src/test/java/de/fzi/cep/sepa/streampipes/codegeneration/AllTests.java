package de.fzi.cep.sepa.streampipes.codegeneration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fzi.cep.sepa.streampipes.codegeneration.flink.*;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ImplementationGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ParametersGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.ProgramGeneratorTest;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.*;

@RunWith(Suite.class)
@SuiteClasses({ FlinkControllerGeneratorTest.class, UtilsTest.class, InitGeneratorTest.class, XmlGeneratorTest.class,
		ProgramGeneratorTest.class, ImplementationGeneratorTest.class, ParametersGeneratorTest.class,
		ConfigGeneratorTest.class , DirectoryBuilderTest.class})
public class AllTests {

}
