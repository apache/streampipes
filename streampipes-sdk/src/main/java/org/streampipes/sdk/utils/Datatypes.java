package org.streampipes.sdk.utils;

import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.net.URI;

public enum Datatypes {

    Integer(XSD._integer),
    Long(XSD._long),
    Float(XSD._float),
    Boolean(XSD._boolean),
    String(XSD._string),
    Double(XSD._double),
    Number(URI.create(SO.Number)),
    Sequence(XSD._sequence);

    private URI uri;

    Datatypes(URI uri) {
        this.uri = uri;
    }

    public String toString() {
        return uri.toString();
    }
}
