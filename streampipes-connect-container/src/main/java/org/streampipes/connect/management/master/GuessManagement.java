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

package org.streampipes.connect.management.master;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;

public class GuessManagement {

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {

        Adapter adapter = AdapterRegistry.getAdapter(adapterDescription);

        GuessSchema guessSchema;
        try {
            guessSchema = adapter.getSchema(adapterDescription);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (Exception e) {
            throw new AdapterException("Unknown Error: " + e);
        }

        return guessSchema;

    }

    public void guessFormat() {
        // TODO implement
    }


    public void  guessFormatDescription() {
        // TODO implement
    }


}
