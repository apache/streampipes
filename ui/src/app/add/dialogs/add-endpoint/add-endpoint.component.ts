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

import { Component, OnInit } from '@angular/core';
import { AddService } from '../../services/add.service';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-add-endpoint-dialog',
    templateUrl: './add-endpoint.component.html',
    styleUrls: ['./add-endpoint.component.scss'],
})
export class AddEndpointComponent implements OnInit {
    rdfEndpoints: any;
    addSelected: any;
    newEndpoint: any;

    endpointsChanged = false;

    constructor(
        private addService: AddService,
        private dialogRef: DialogRef<AddEndpointComponent>,
    ) {
        this.rdfEndpoints = [];
        this.addSelected = false;
        this.newEndpoint = {};
    }

    ngOnInit() {
        this.loadRdfEndpoints();
    }

    showAddInput() {
        this.addSelected = true;
    }

    loadRdfEndpoints() {
        this.addService.getRdfEndpoints().subscribe(rdfEndpoints => {
            this.rdfEndpoints = rdfEndpoints;
        });
    }

    addRdfEndpoint(rdfEndpoint) {
        console.log(rdfEndpoint);
        this.addService.addRdfEndpoint(rdfEndpoint).subscribe(() => {
            this.loadRdfEndpoints();
            this.endpointsChanged = true;
        });
    }

    removeRdfEndpoint(rdfEndpointId) {
        this.addService.removeRdfEndpoint(rdfEndpointId).subscribe(() => {
            this.loadRdfEndpoints();
            this.endpointsChanged = true;
        });
    }

    close() {
        this.dialogRef.close(this.endpointsChanged);
    }
}
