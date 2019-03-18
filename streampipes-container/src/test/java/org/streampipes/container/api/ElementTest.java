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

package org.streampipes.container.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;

import java.util.HashMap;
import java.util.Map;

public class ElementTest {;
    @Test
    public void getByIdTest() {
        String id = "sepapathName1";
        Map<String, Declarer> declarers = new HashMap<>();
        declarers.put(id, getDeclarerImpl(id));
        declarers.put("sepapathName2", getDeclarerImpl("sepapathName2"));
        TestElementImpl elem = new TestElementImpl();
        elem.setDeclarers(declarers);

        NamedStreamPipesEntity namedSEPAElement = elem.getById(id);

        assertEquals("sepaname", namedSEPAElement.getName());
        assertEquals("sepadescription", namedSEPAElement.getDescription());
    }

    @Test
    public void getByIdIsNullTest() {
        TestElementImpl elem = new TestElementImpl();
        elem.setDeclarers(new HashMap<>());

        NamedStreamPipesEntity actual = elem.getById("");
        assertNull(actual);
    }

    @Test
    public void toJsonLdNullTest() {
        SepElement sep = new SepElement();
        assertEquals("{}", sep.toJsonLd(null));
    }


    private DeclarerImpl getDeclarerImpl(String id) {
        return new DeclarerImpl(id);
    }


    private class DeclarerImpl implements InvocableDeclarer<DataProcessorDescription, DataProcessorInvocation> {
        private String id;

        public DeclarerImpl(String id) {
            this.id = id;
        }

        @Override
        public Response invokeRuntime(DataProcessorInvocation invocationGraph) {
            return null;
        }

        @Override
        public Response detachRuntime(String pipelineId) {
            return null;
        }

        @Override
        public DataProcessorDescription declareModel() {
            return new DataProcessorDescription(id, "sepaname", "sepadescription", "sepaiconUrl");
        }
    }

    private class TestElementImpl extends Element<Declarer> {
        private Map<String, Declarer> declarers = new HashMap<>();

        public Map<String, Declarer> getDeclarers() {
            return declarers;
        }

        public void setDeclarers(Map<String, Declarer> declarers) {
            this.declarers = declarers;
        }

        @Override
        protected Map<String, Declarer> getElementDeclarers() {
            return declarers;
        }
    }
}
