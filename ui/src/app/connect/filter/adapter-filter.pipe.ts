/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { AdapterDescription } from '@streampipes/platform-services';
import { AdapterFilterSettingsModel } from '../model/adapter-filter-settings.model';
import { ConnectService } from '../services/connect.service';

@Pipe({ name: 'adapterFilter' })
@Injectable({ providedIn: 'root' })
export class AdapterFilterPipe implements PipeTransform {
    constructor(private connectService: ConnectService) {}

    transform(
        adapterDescriptions: AdapterDescription[],
        activeFilters: AdapterFilterSettingsModel,
    ): AdapterDescription[] {
        if (!activeFilters) {
            return adapterDescriptions;
        } else {
            return adapterDescriptions.filter(a =>
                this.meetsFilterCondition(a, activeFilters),
            );
        }
    }

    private meetsFilterCondition(
        adapterDescription: AdapterDescription,
        activeFilters: AdapterFilterSettingsModel,
    ): boolean {
        return (
            this.meetsFilterCategoryCondition(
                adapterDescription,
                activeFilters.selectedCategory,
            ) &&
            this.meetsFilterTextCondition(
                adapterDescription,
                activeFilters.textFilter,
            )
        );
    }

    private meetsFilterCategoryCondition(
        adapterDescription: AdapterDescription,
        selectedCategory: string,
    ): boolean {
        if (selectedCategory === 'All') {
            return true;
        } else {
            return adapterDescription.category.indexOf(selectedCategory) !== -1;
        }
    }

    private meetsFilterTextCondition(
        adapterDescription: AdapterDescription,
        filterTerm: string,
    ): boolean {
        if (filterTerm === undefined || filterTerm === '') {
            return true;
        } else {
            if (adapterDescription.name == null) {
                return true;
            } else {
                adapterDescription.name.replace(' ', '_');
                return (
                    adapterDescription.name
                        .toLowerCase()
                        .includes(filterTerm.toLowerCase()) ||
                    adapterDescription.description
                        .toLowerCase()
                        .includes(filterTerm.toLowerCase())
                );
            }
        }
    }
}
