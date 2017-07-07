package org.streampipes.pe.sources.samples.mnist;

import org.junit.Test;

public class MnistStreamTest {
    @Test
    public void executeStream() throws Exception {

        MnistStream mn = new MnistStream();
        mn.executeStream();

    }

}