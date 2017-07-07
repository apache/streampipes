package org.streampipes.pe.sources.samples.taxi.test;

import org.streampipes.pe.sources.samples.taxi.NYCTaxiStream;

/**
 * Created by robin on 19.03.15.
 */
public class TestTaxiActiveMQ {
    public static void main(String args[]) {
            NYCTaxiStream stream = new NYCTaxiStream();
            stream.executeStream();
    }
}
