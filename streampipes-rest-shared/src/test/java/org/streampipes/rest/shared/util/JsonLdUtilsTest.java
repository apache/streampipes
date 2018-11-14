/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.shared.util;

import org.junit.Test;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.vocabulary.StreamPipes;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JsonLdUtilsTest {

    @Test
    public void fromToJsonLdSimple() {
        FormatDescriptionList object = new FormatDescriptionList();
        String jsonLD = JsonLdUtils.toJsonLD(object);

        FormatDescriptionList result = JsonLdUtils.fromJsonLd(jsonLD, FormatDescriptionList.class);

        assertEquals(result.getUri(), "http://bla.de#2");
        assertNotNull(result.getList());
        assertEquals(result.getList().size(), 0);
    }

    @Test
    public void fromToJsonLdSimpleComplexTopElement() {
        List<AdapterDescription> list = Arrays.asList(
                new GenericAdapterStreamDescription(),
                new GenericAdapterSetDescription());

        AdapterDescriptionList object = new AdapterDescriptionList(list);
        String jsonLD = JsonLdUtils.toJsonLD(object);

        AdapterDescriptionList result = JsonLdUtils.fromJsonLd(jsonLD, AdapterDescriptionList.class, StreamPipes.ADAPTER_DESCRIPTION_LIST);

        assertEquals(result.getUri(), "http://streampipes.org/adapterlist");
        assertNotNull(result.getList());
        assertEquals(2, result.getList().size());
        assertEquals("http://id.de#3", result.getList().get(0).getUri());
        assertEquals("name1", result.getList().get(0).getName());
        assertEquals("http://id.de#4", result.getList().get(1).getUri());
        assertEquals("name2", result.getList().get(1).getName());
    }

    @Test
    public void fromToJsonLdComplex() {
        List<FormatDescription> list = Arrays.asList(
                new FormatDescription("http://id.de#3", "name1", ""),
                new FormatDescription("http://id.de#4", "name2", ""));

        FormatDescriptionList object = new FormatDescriptionList(list);
        String jsonLD = JsonLdUtils.toJsonLD(object);

        System.out.println(jsonLD);

        FormatDescriptionList result = JsonLdUtils.fromJsonLd(jsonLD, FormatDescriptionList.class);

        assertEquals("http://bla.de#2", result.getUri());
        assertNotNull(result.getList());
        assertEquals(2, result.getList().size());
        assertEquals(result.getList().get(0).getUri(), "http://id.de#3");
        assertEquals(result.getList().get(0).getName(), "name1");
        assertEquals(result.getList().get(1).getUri(), "http://id.de#4");
        assertEquals(result.getList().get(1).getName(), "name2" );
    }
}