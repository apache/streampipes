/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.uo;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitOntologyFactory {

  private static UnitOntologyFactory factory = null;

  private static final Logger LOG = LoggerFactory.getLogger(UnitOntologyFactory.class);

  @SuppressWarnings("serial")
  private Map<String, String> uo2qudt = new HashMap<String, String>() {
    // a helper method
    String longURI(String shortened) {
      if (shortened.startsWith("uo:")) {
        return "http://purl.obolibrary.org/obo/" + shortened.substring(3);
      }
      if (shortened.startsWith("ops:")) {
        return "http://www.openphacts.org/units/" + shortened.substring(4);
      }
      return null;
    }

    // the next defines all mappings from the Unit Ontology to the QUDT
    {
      put(longURI("uo:EFO_0004374"), longURI("ops:MilligramPerDeciliter"));
      put(longURI("uo:EFO_0004385"), longURI("ops:PicogramPerMilliliter"));
      put(longURI("uo:UO_0000009"), longURI("qudt:Kilogram"));
      put(longURI("uo:UO_0000010"), longURI("qudt:SecondTime"));
      put(longURI("uo:UO_0000015"), longURI("qudt:Centimeter"));
      put(longURI("uo:UO_0000016"), longURI("qudt:Millimeter"));
      put(longURI("uo:UO_0000017"), longURI("qudt:Micrometer"));
      put(longURI("uo:UO_0000018"), longURI("ops:Nanometer"));
      put(longURI("uo:UO_0000021"), longURI("qudt:Gram"));
      put(longURI("uo:UO_0000022"), longURI("ops:Milligram"));
      put(longURI("uo:UO_0000023"), longURI("ops:Microgram"));
      put(longURI("uo:UO_0000024"), longURI("ops:Nanogram"));
      put(longURI("uo:UO_0000025"), longURI("ops:Picogram"));
      put(longURI("uo:UO_0000026"), longURI("ops:Femtogram"));
      put(longURI("uo:UO_0000027"), longURI("qudt:DegreeCelsius"));
      put(longURI("uo:UO_0000028"), longURI("qudt:Millisecond"));
      put(longURI("uo:UO_0000031"), longURI("qudt:MinuteTime"));
      put(longURI("uo:UO_0000032"), longURI("qudt:Hour"));
      put(longURI("uo:UO_0000033"), longURI("qudt:Day"));
      put(longURI("uo:UO_0000039"), longURI("qudt:Micromole"));
      put(longURI("uo:UO_0000040"), longURI("qudt:Millimole"));
      put(longURI("uo:UO_0000041"), longURI("qudt:Nanomole"));
      put(longURI("uo:UO_0000042"), longURI("qudt:Picomole"));
      put(longURI("uo:UO_0000043"), longURI("qudt:Femtomole"));
      put(longURI("uo:UO_0000062"), longURI("ops:Molar"));
      put(longURI("uo:UO_0000063"), longURI("ops:Millimolar"));
      put(longURI("uo:UO_0000064"), longURI("ops:Micromolar"));
      put(longURI("uo:UO_0000065"), longURI("ops:Nanomolar"));
      put(longURI("uo:UO_0000066"), longURI("ops:Picomolar"));
      put(longURI("uo:UO_0000073"), longURI("ops:Femtomolar"));
      put(longURI("uo:UO_0000098"), longURI("ops:Milliliter"));
      put(longURI("uo:UO_0000099"), longURI("qudt:Liter"));
      put(longURI("uo:UO_0000101"), longURI("ops:Microliter"));
      put(longURI("uo:UO_0000169"), longURI("ops:PartsPerMillion"));
      put(longURI("uo:UO_0000173"), longURI("ops:GramPerMilliliter"));
      put(longURI("uo:UO_0000175"), longURI("ops:GramPerLiter"));
      put(longURI("uo:UO_0000176"), longURI("ops:MilligramPerMilliliter"));
      put(longURI("uo:UO_0000187"), longURI("qudt:Percent"));
      put(longURI("uo:UO_0000197"), longURI("ops:LiterPerKilogram"));
      put(longURI("uo:UO_0000198"), longURI("ops:MilliliterPerKilogram"));
      put(longURI("uo:UO_0000271"), longURI("ops:MicroliterPerMinute"));
      put(longURI("uo:UO_0000272"), longURI("qudt:MillimeterOfMercury"));
      put(longURI("uo:UO_0000274"), longURI("ops:MicrogramPerMilliliter"));
      put(longURI("uo:UO_0000275"), longURI("ops:NanogramPerMilliliter"));
      put(longURI("uo:UO_0000308"), longURI("ops:MilligramPerKilogram"));
//		    put(longURI("uo:UO_0000311"), longURI(""));
    }
  };
  private Map<String, String> qudt2uo = null;

  private UnitOntologyFactory() {
    // not backed up by a formal ontology (at this moment; see UnitFactory's constructor)
    // instead, it uses defined mappings in uo2qudt

    // also make the reverse mapping table
    qudt2uo = new HashMap<>();
    for (String keyURI : uo2qudt.keySet()) {
      qudt2uo.put(uo2qudt.get(keyURI), keyURI);
    }
  }

  public static UnitOntologyFactory getInstance() {
    if (factory == null) {
      factory = new UnitOntologyFactory();
    }
    return factory;
  }

  private static URI asURI(String resource) {
    try {
      return new URI(resource);
    } catch (URISyntaxException exception) {
      return null;
    }
  }

  public Unit getUnit(String resource) {
    LOG.info("resource:" + resource);
    return getUnit(asURI(resource));
  }

  public Unit getUnit(URI resource) {
    if (resource == null) {
      throw new IllegalArgumentException("The URI cannot be null");
    }

    URI mappedURI = asURI(uo2qudt.get(resource.toString()));
    if (mappedURI != null) {
      return UnitFactory.getInstance().getUnit(mappedURI);
    } else {
      return null;
    }
  }

  public List<String> getURIs(String type) {
    URI uri;
    try {
      uri = new URI(type);
    } catch (URISyntaxException exception) {
      throw new IllegalStateException("Invalid URI: " + type, exception);
    }
    return getURIs(uri);
  }

  public List<String> getURIs(URI type) {
    List<String> uris = new ArrayList<>();

    List<String> qudtURIs = UnitFactory.getInstance().getURIs(type);
    for (String qudtString : qudtURIs) {
      String uoURI = qudt2uo.get(qudtString);
      if (uoURI != null) {
        uris.add(uoURI);
      }
    }
    return uris;
  }
}
