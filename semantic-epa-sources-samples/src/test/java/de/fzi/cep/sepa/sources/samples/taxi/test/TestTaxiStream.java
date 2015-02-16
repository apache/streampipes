package de.fzi.cep.sepa.sources.samples.taxi.test;

import javax.jms.JMSException;

import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiStream;

public class TestTaxiStream {

	public static void main(String[] args) throws JMSException
	{
		new NYCTaxiStream().executeStream();
	}
}
