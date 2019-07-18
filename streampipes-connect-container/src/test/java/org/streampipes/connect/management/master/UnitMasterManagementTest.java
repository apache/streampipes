/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.management.master;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.unit.UnitDescription;
import org.streampipes.units.UnitProvider;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UnitProvider.class, UnitFactory.class })
public class UnitMasterManagementTest {

    @Test(expected = AdapterException.class)
    public void URLisNull() throws AdapterException {
        UnitMasterManagement unitMasterManagement = new UnitMasterManagement();
        unitMasterManagement.getFittingUnits(getUnitDescription("",null));
    }

    @Test(expected = AdapterException.class)
    public void invalidURL() throws AdapterException {
        UnitProvider unitProvider = mock(UnitProvider.INSTANCE.getClass());
        when(unitProvider.getUnit(anyString())).thenThrow(new IllegalStateException());

        UnitMasterManagement unitMasterManagement = new UnitMasterManagement();
        unitMasterManagement.getFittingUnits(getUnitDescription("","http://test"));
    }

    @Test
    public void getFittingUnitsEmpty() throws Exception {
        UnitProvider unitProvider = mock(UnitProvider.INSTANCE.getClass());
        when(unitProvider.getUnit(anyString())).thenReturn(new Unit());
        when(unitProvider.getUnitsByType(any())).thenReturn((new ArrayList<>()));
        Whitebox.setInternalState(UnitProvider.class, "INSTANCE", unitProvider);

        UnitMasterManagement unitMasterManagement = new UnitMasterManagement();
        String jsonResult = unitMasterManagement.getFittingUnits(getUnitDescription("",""));
        assertEquals("[]", jsonResult);
    }

    @Test
    public void getFittingUnitsUnitsEmpty() throws Exception {
        UnitProvider unitProvider = mock(UnitProvider.INSTANCE.getClass());
        when(unitProvider.getUnit(anyString())).thenReturn(new Unit());

        List<Unit> unitList = new ArrayList<>(2);
        unitList.add(new Unit());
        unitList.add(new Unit());

        when(unitProvider.getUnitsByType(any())).thenReturn((unitList));
        Whitebox.setInternalState(UnitProvider.class, "INSTANCE", unitProvider);

        UnitMasterManagement unitMasterManagement = new UnitMasterManagement();
        String jsonResult = unitMasterManagement.getFittingUnits(getUnitDescription("",""));
        assertEquals("[]", jsonResult);
    }

    @Test
    public void getFittingUnitsUnits() throws Exception {
        UnitProvider unitProvider = mock(UnitProvider.INSTANCE.getClass());
        when(unitProvider.getUnit(anyString())).thenReturn(new Unit());

        List<Unit> unitList = new ArrayList<>(2);
        Unit unit = new Unit();
        unit.setLabel("A");
        unit.setResource(new URI("http://A"));
        unitList.add(unit);
        unit = new Unit();
        unit.setLabel("A");
        unit.setResource(new URI("http://A"));
        unitList.add(unit);
        unitList.add(new Unit());

        when(unitProvider.getUnitsByType(any())).thenReturn((unitList));
        Whitebox.setInternalState(UnitProvider.class, "INSTANCE", unitProvider);

        UnitMasterManagement unitMasterManagement = new UnitMasterManagement();
        String jsonResult = unitMasterManagement.getFittingUnits(getUnitDescription("",""));
        assertEquals("[{\"resource\":\"http://A\",\"label\":\"A\"},{\"resource\":\"http://A\",\"label\":\"A\"}]", jsonResult);
    }


    private UnitDescription getUnitDescription(String label, String ressource) {
        UnitDescription unitDescription = new UnitDescription();
        unitDescription.setLabel(label);
        unitDescription.setResource(ressource);
        return unitDescription;
    }

}