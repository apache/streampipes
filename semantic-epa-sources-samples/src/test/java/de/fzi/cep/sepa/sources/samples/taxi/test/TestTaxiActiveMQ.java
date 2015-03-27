package de.fzi.cep.sepa.sources.samples.taxi.test;

import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiStream;

import javax.jms.JMSException;

/**
 * Created by robin on 19.03.15.
 */
public class TestTaxiActiveMQ {
    public static void main(String args[]) {
        try {
            NYCTaxiStream stream = new NYCTaxiStream();
            stream.executeStream();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
