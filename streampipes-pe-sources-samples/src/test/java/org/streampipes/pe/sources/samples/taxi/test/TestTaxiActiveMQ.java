package org.streampipes.pe.sources.samples.taxi.test;

import org.streampipes.pe.sources.samples.taxi.NYCTaxiStream;

public class TestTaxiActiveMQ {
    public static void main(String args[]) {
            NYCTaxiStream stream = new NYCTaxiStream();
            stream.executeStream();
    }
}
