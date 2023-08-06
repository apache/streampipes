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

import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ConfigurationService } from '../../shared/configuration.service';
import {
    animate,
    state,
    style,
    transition,
    trigger,
} from '@angular/animations';
import { SpServiceConfiguration } from '@streampipes/platform-services';

@Component({
    selector: 'sp-extensions-service-configuration',
    templateUrl: './extensions-service-configuration.component.html',
    styleUrls: ['./extensions-service-configuration.component.scss'],
    animations: [
        trigger('detailExpand', [
            state(
                'collapsed',
                style({ height: '0px', minHeight: '0', display: 'none' }),
            ),
            state('expanded', style({ height: '*' })),
            transition(
                'expanded <=> collapsed',
                animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'),
            ),
        ]),
    ],
})
export class SpExtensionsServiceConfigurationComponent {
    displayedColumns: string[] = ['group', 'name', 'action'];
    @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
    dataSource = new MatTableDataSource<SpServiceConfiguration>();

    expandedElement: any;
    consulServices: SpServiceConfiguration[];

    constructor(private configurationService: ConfigurationService) {
        this.getConsulServices();
    }

    getConsulServices(): void {
        this.configurationService.getExtensionsServiceConfigs().subscribe(
            response => {
                const sortedServices = this.sort(response);
                this.consulServices = sortedServices;
                this.dataSource.data = sortedServices;
                console.log(this.consulServices);
            },
            error => {
                console.error(error);
            },
        );
    }

    sort(consulServices: SpServiceConfiguration[]): SpServiceConfiguration[] {
        if (!consulServices || consulServices.length === 0) {
            return null;
        }

        consulServices.sort(
            (a: SpServiceConfiguration, b: SpServiceConfiguration) => {
                if (a.serviceGroup < b.serviceGroup) {
                    return -1;
                } else if (a.serviceGroup > b.serviceGroup) {
                    return 1;
                } else {
                    return 0;
                }
            },
        );
        return consulServices;
    }

    updateConsulService(config: SpServiceConfiguration): void {
        this.configurationService
            .updateExtensionsServiceConfigs(config)
            .subscribe(
                () => {
                    this.getConsulServices();
                },
                error => {
                    console.error(error);
                },
            );
    }

    expand(element: SpServiceConfiguration) {
        if (this.expandedElement === element) {
            this.expandedElement = undefined;
        } else {
            this.expandedElement = element;
        }
    }
}
