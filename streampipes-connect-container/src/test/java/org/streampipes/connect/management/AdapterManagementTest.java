package org.streampipes.connect.management;

import org.junit.Test;
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.model.modelconnect.AdapterStreamDescription;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterManagementTest {


    @Test
    public void stopStreamAdapterFail() {
        String expected = "Adapter with id http://test.de was not found in this container and cannot be stopped.";
        AdapterStreamDescription asd = new AdapterStreamDescription();
        asd.setUri("http://test.de");

        AdapterManagement adapterManagement = new AdapterManagement();

        String result = adapterManagement.stopStreamAdapter(asd);
        assertEquals(expected, result);
    }

    @Test
    public void stopStreamAdapterSuccess() {
        String id = "http://test.de";
        AdapterStreamDescription asd = new AdapterStreamDescription();
        asd.setUri(id);

        Adapter adapter = mock(Adapter.class);

        RunningAdapterInstances.INSTANCE.addAdapter(id, adapter);

        AdapterManagement adapterManagement = new AdapterManagement();

        String result = adapterManagement.stopStreamAdapter(asd);
        assertEquals("", result);
    }
}