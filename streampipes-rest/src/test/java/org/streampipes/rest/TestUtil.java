package org.streampipes.rest;

public class TestUtil {

    public static String getMinimalStreamAdapterJsonLD() {
        return getMinimalAdapterJsonLD("sp:AdapterStreamDescription");
    }

    public static String getMinimalSetAdapterJsonLD() {
        return getMinimalAdapterJsonLD("sp:AdapterSetDescription");
    }

    public static String getMinimalAdapterJsonLD(String type) {
        return "{\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"http://test.de/1\",\n" +
                "    \"@type\" : \""+ type + "\",\n" +
                "    \"http://www.w3.org/2000/01/rdf-schema#label\" : \"TestAdapterDescription\",\n" +
                "    \"sp:hasDataSet\" : {\n" +
                "      \"@id\" : \"urn:fzi.de:eventstream:lDVmMJ\"\n" +
                "    },\n" +
                "    \"sp:hasUri\" : \"http://test.de/1\"\n" +
                "  }, {\n" +
                "    \"@id\" : \"urn:fzi.de:eventstream:lDVmMJ\",\n" +
                "    \"@type\" : \"sp:DataSet\",\n" +
                "    \"sp:hasUri\" : \"urn:fzi.de:eventstream:lDVmMJ\"\n" +
                "  } ],\n" +
                "  \"@context\" : {\n" +
                "    \"sp\" : \"https://streampipes.org/vocabulary/v1/\",\n" +
                "    \"ssn\" : \"http://purl.oclc.org/NET/ssnx/ssn#\",\n" +
                "    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n" +
                "    \"empire\" : \"urn:clarkparsia.com:empire:\",\n" +
                "    \"spi\" : \"urn:streampipes.org:spi:\"\n" +
                "  }\n" +
                "}";
    }

    public static String getMinimalDataSet() {
        return "{\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"http://bla.de/1\",\n" +
                "    \"@type\" : \"sp:DataSet\",\n" +
                "    \"http://www.w3.org/2000/01/rdf-schema#description\" : \"des\",\n" +
                "    \"http://www.w3.org/2000/01/rdf-schema#label\" : \"name\",\n" +
                "    \"sp:hasSchema\" : {\n" +
                "      \"@id\" : \"spi:eventschema:adbgUv\"\n" +
                "    },\n" +
                "    \"sp:hasUri\" : \"http://bla.de/1\"\n" +
                "  }, {\n" +
                "    \"@id\" : \"spi:eventschema:adbgUv\",\n" +
                "    \"@type\" : \"sp:EventSchema\",\n" +
                "    \"sp:hasElementName\" : \"urn:streampipes.org:spi:eventschema:adbgUv\"\n" +
                "  } ],\n" +
                "  \"@context\" : {\n" +
                "    \"sp\" : \"https://streampipes.org/vocabulary/v1/\",\n" +
                "    \"ssn\" : \"http://purl.oclc.org/NET/ssnx/ssn#\",\n" +
                "    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n" +
                "    \"empire\" : \"urn:clarkparsia.com:empire:\",\n" +
                "    \"spi\" : \"urn:streampipes.org:spi:\"\n" +
                "  }\n" +
                "}";
    }

}
