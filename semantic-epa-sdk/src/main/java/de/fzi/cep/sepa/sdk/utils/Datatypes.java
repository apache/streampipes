package de.fzi.cep.sepa.sdk.utils;

import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

import java.net.URI;

/**
 * Created by riemer on 06.12.2016.
 */
public enum Datatypes {

    Integer(XSD._integer),
    Float(XSD._float),
    Boolean(XSD._boolean),
    String(XSD._string),
    Double(XSD._double),
    Number(URI.create(SO.Number));

    private URI uri;

    Datatypes(URI uri) {
        this.uri = uri;
    }

    public String toString() {
        return uri.toString();
    }
}
