package de.fzi.cep.sepa.manager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fzi.cep.sepa.manager.matching.v2.TestDatatypeMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestDomainPropertyMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestElementVerification;
import de.fzi.cep.sepa.manager.matching.v2.TestFormatMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestGroundingMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestMeasurementUnitMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestPrimitivePropertyMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestProtocolMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestSchemaMatch;
import de.fzi.cep.sepa.manager.matching.v2.TestStreamMatch;

@RunWith(Suite.class)
@SuiteClasses({ TestDatatypeMatch.class, 
	TestDomainPropertyMatch.class, 
	TestElementVerification.class, 
	TestFormatMatch.class, 
	TestGroundingMatch.class, 
	TestMeasurementUnitMatch.class, 
	TestPrimitivePropertyMatch.class, 
	TestProtocolMatch.class, 
	TestSchemaMatch.class,
	TestStreamMatch.class})

public class AllTests {

}
