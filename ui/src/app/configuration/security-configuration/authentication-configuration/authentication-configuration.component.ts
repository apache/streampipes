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

import { Component } from '@angular/core';
import { ConfigurationService } from '../../shared/configuration.service';
import * as FileSaver from 'file-saver';

@Component({
    selector: 'sp-authentication-configuration',
    templateUrl: './authentication-configuration.component.html',
    styleUrls: ['./authentication-configuration.component.scss'],
})
export class SecurityAuthenticationConfigurationComponent {
    constructor(private configurationService: ConfigurationService) {}

    generateKeyPair() {
        this.configurationService.generateKeyPair().subscribe(result => {
            console.log(result);
            this.saveKeyfile('public.key', result[0]);
            this.saveKeyfile('private.pem', result[1]);
        });
    }

    saveKeyfile(filename: string, content: string) {
        const blob = new Blob([content], { type: 'text/plain' });
        FileSaver.saveAs(blob, filename);
    }
}
