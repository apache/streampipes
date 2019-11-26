/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import * as angular from 'angular';

class SelectFilter {

    static selectFilter() {
        return (pipelineElements, selectedOptions) => {
            var filteredElements = [];
            angular.forEach(pipelineElements, pe => {
                if (!pe.category || pe.category.length === 0) {
                    filteredElements.push(pe);
                }
                angular.forEach(selectedOptions, so => {
                    if (pe.category.indexOf(so) !== -1) {
                        filteredElements.push(pe);
                    }
                })
            })
            return filteredElements;
        }
    }
}

export default SelectFilter.selectFilter;

