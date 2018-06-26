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

package org.streampipes.connect.firstconnector.guess;

//TODO Replace this class with DomainPropertyProbabilityList
public class PropertyGuessResults {
    private PropertyGuesses[] result;

    public PropertyGuessResults() {
    }


    public PropertyGuessResults(PropertyGuesses[] result) {
        this.result = result;
    }

    public PropertyGuesses[] getResult() {
        return result;
    }

    public void setResult(PropertyGuesses[] result) {
        this.result = result;
    }
}
