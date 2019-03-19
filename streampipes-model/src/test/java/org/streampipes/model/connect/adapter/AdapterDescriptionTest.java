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

package org.streampipes.model.connect.adapter;

import org.junit.Before;
import org.junit.Test;
import org.streampipes.model.connect.rules.Schema.CreateNestedRuleDescription;
import org.streampipes.model.connect.rules.Schema.DeleteRuleDescription;
import org.streampipes.model.connect.rules.Schema.MoveRuleDescription;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AdapterDescriptionTest {

    AdapterDescription adapterDescription;

    @Before
    public void init() {
        adapterDescription = new AdapterDescription() {
        };

        List rules = new ArrayList<>();
        rules.add(new CreateNestedRuleDescription());
        rules.add(new CreateNestedRuleDescription());
        rules.add(new DeleteRuleDescription());
        rules.add(new UnitTransformRuleDescription());
        rules.add(new CreateNestedRuleDescription());
        rules.add(new CreateNestedRuleDescription());
        rules.add(new UnitTransformRuleDescription());
        rules.add(new MoveRuleDescription());
        rules.add(new CreateNestedRuleDescription());

        adapterDescription.setRules(rules);
    }

    @Test
    public void getValueRules() {
        assertEquals(2, adapterDescription.getValueRules().size());
    }

    @Test
    public void getStreamRules() {
        assertEquals(0, adapterDescription.getStreamRules().size());
    }

    @Test
    public void getSchemaRules() {
        assertEquals(7, adapterDescription.getSchemaRules().size());
    }
}