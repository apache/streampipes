/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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

package org.streampipes.manager.matching.v2;

import org.junit.Test;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.schema.EventPropertyList;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListPropertyMatchTest {


    @Test
    public void matchSameDomainProperty() throws Exception {
        List<URI> domainProperties = new ArrayList<>();
        domainProperties.add(URI.create("http://test.org/property"));

        EventPropertyList offer = new EventPropertyList();
        offer.setDomainProperties(domainProperties);
        EventPropertyList requirement = new EventPropertyList();
        requirement.setDomainProperties(domainProperties);

        List<MatchingResultMessage> errorLog = new ArrayList<>();

        boolean result = new ListPropertyMatch().match(offer, requirement, errorLog);

        assertTrue(result);
    }

    @Test
    public void matchListWithNoFurtherRequirements() throws Exception {

        EventPropertyList offer = new EventPropertyList();
        EventPropertyList requirement = new EventPropertyList();

        List<MatchingResultMessage> errorLog = new ArrayList<>();

        boolean result = new ListPropertyMatch().match(offer, requirement, errorLog);

        assertTrue(result);
    }

}