/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

//@RunWith(Suite.class)
//@SuiteClasses({ FlinkSepaControllerGeneratorTest.class, UtilsTest.class, InitGeneratorTest.class,
//		XmlGeneratorTest.class, FlinkSepaProgramGeneratorTest.class, ImplementationGeneratorTest.class,
//		ParametersGeneratorTest.class, ConfigGeneratorTest.class, DirectoryBuilderTest.class,
//		FlinkSecControllerGeneratorTest.class, FlinkSecProgramGeneratorTest.class})
//public class AllTests {
//
//}
