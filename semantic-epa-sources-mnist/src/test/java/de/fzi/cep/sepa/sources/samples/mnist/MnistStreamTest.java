package de.fzi.cep.sepa.sources.samples.mnist;

import org.junit.Test;

import static org.junit.Assert.*;

public class MnistStreamTest {
    @Test
    public void executeStream() throws Exception {

        MnistStream mn = new MnistStream();
        mn.executeStream();

    }

}