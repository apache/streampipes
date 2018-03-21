package org.streampipes.model.modelconnect;


import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;

import javax.persistence.Entity;
import java.io.IOException;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:Person")
@Entity
public class Person extends NamedStreamPipesEntity {

    @RdfProperty("sp:name")
    private String name;

    @RdfProperty("sp:friend")
    private Person friend;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, Person friend) {
        this.name = name;
//        this.friend = friend;
    }

    public static void main(String[] args) {

        String personSerialized = "{\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"empire:AFFE256A3ED1AE42A4A366B611AD4275\",\n" +
                "    \"@type\" : \"http://sepa.event-processing.org/streampipes#Person\",\n" +
                "    \"http://sepa.event-processing.org/streampipes#name\" : \"Peter\"\n" +
                "  }, {\n" +
                "    \"@id\" : \"empire:CE0C8E6BA79470D3FC557BF01CC73DDF\",\n" +
                "    \"@type\" : \"http://sepa.event-processing.org/streampipes#Person\",\n" +
                "    \"http://sepa.event-processing.org/streampipes#friend\" : {\n" +
                "      \"@id\" : \"empire:AFFE256A3ED1AE42A4A366B611AD4275\"\n" +
                "    },\n" +
                "    \"http://sepa.event-processing.org/streampipes#name\" : \"Hans\"\n" +
                "  } ],\n" +
                "  \"@context\" : {\n" +
                "    \"sepa\" : \"http://sepa.event-processing.org/sepa#\",\n" +
                "    \"ssn\" : \"http://purl.oclc.org/NET/ssnx/ssn#\",\n" +
                "    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n" +
                "    \"empire\" : \"urn:clarkparsia.com:empire:\",\n" +
                "    \"fzi\" : \"urn:fzi.de:sepa:\"\n" +
                "  }\n" +
                "}";

        Person p = new Person("Hans", new Person("Peter"));

//
//        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
//        String result = null;
//        try {
//            Person x = jsonLdTransformer.fromJsonLd(personSerialized, Person.class);
////            result = Utils.asString(jsonLdTransformer.toJsonLd(p));
//        } catch (RepositoryException e) {
//            e.printStackTrace();
//        } catch (RDFParseException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        System.out.println(result);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getFriend() {
        return friend;
    }

//    public void setFriend(Person friend) {
//        this.friend = friend;
//    }
}
