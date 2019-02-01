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
package org.streampipes.connect.rest.master;

import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.UnitMasterManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.connect.unit.UnitDescription;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitResourceTest extends ConnectContainerResourceTest {

    private Server server;

    private UnitResource unitResource;
    private UnitMasterManagement unitMasterManagement;

    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        this.unitResource = new UnitResource();
        this.server = getMasterServer(unitResource);

        unitMasterManagement = mock(UnitMasterManagement.class);
        unitResource.setUnitMasterManagement(unitMasterManagement);
    }

    @After
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getApi() {
        return "/api/v1/tex@fzi.de/master/unit";
    }

    @Test
    public void getFittingUnitsSucess() throws Exception {
        when(unitMasterManagement.getFittingUnits(any())).thenReturn(getMultiData());

        Response res = given()
                .body(getData())
                .when()
                .contentType("application/json")
                .post(getApi());


        res.then()
                .assertThat()
                .statusCode(200);

        String resultString = res.body().print();

        assertEquals(getMultiData(), resultString);
    }

    @Test
    public void getFittingUnitsEmptySucess() throws Exception {
        when(unitMasterManagement.getFittingUnits(any())).thenReturn("{}");

        Response res = given()
                .body(getData())
                .when()
                .contentType("application/json")
                .post(getApi());


        res.then()
                .assertThat()
                .statusCode(200);

        String resultString = res.body().print();

        assertEquals("{}", resultString);
    }

    @Test
    public void getFittingUnitsFail() throws Exception {
        when(unitMasterManagement.getFittingUnits(any())).thenThrow(new AdapterException());

        Response res = given()
                .body(getData())
                .when()
                .contentType("application/json")
                .post(getApi());


        res.then()
                .assertThat()
                .statusCode(500);
    }

    private String getData() {
        Gson gson = new Gson();
        return gson.toJson(getUnitDescription("Degree Fahrenheit", "http://qudt.org/vocab/unit#DegreeFahrenheit"));
    }

    private String getMultiData() {
        Gson gson = new Gson();
        List list = new ArrayList<>();
        list.add(getUnitDescription("Degree Fahrenheit", "http://qudt.org/vocab/unit#DegreeFahrenheit"));
        list.add(getUnitDescription("Degree Celsius", "http://qudt.org/vocab/unit#DegreeCelsius"));
        return gson.toJson(list);
    }


    private UnitDescription getUnitDescription(String label, String ressource) {
        UnitDescription unitDescription = new UnitDescription();
        unitDescription.setLabel(label);
        unitDescription.setResource(ressource);
        return unitDescription;
    }


}