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

import { Component, Input, OnInit } from '@angular/core';
import { stringify } from 'yaml';
import { MatTab, MatTabChangeEvent, MatTabGroup } from '@angular/material/tabs';
import { MatIconButton } from '@angular/material/button';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';
import { JsonPrettyPrintPipe } from '../pipes/json-pretty-print.pipe';
import { YamlPrettyPrintPipe } from '../pipes/yaml-pretty-print.pipe';

@Component({
    selector: 'sp-configuration-code-panel',
    templateUrl: './configuration-code-panel.component.html',
    styleUrls: ['./configuration-code-panel.component.scss'],
    imports: [
        MatTabGroup,
        MatTab,
        MatIconButton,
        CdkCopyToClipboard,
        MatTooltip,
        MatIcon,
        NgStyle,
        StyleDirective,
        TranslatePipe,
        JsonPrettyPrintPipe,
        YamlPrettyPrintPipe,
    ],
})
export class ConfigurationCodePanelComponent implements OnInit {
    @Input()
    configuration: any;

    @Input()
    maxHeight = '300px';

    configurationYaml: string;
    configurationJson: string;

    currentConfiguration: string;

    ngOnInit() {
        this.configurationYaml = stringify(this.configuration);
        this.configurationJson = JSON.stringify(this.configuration);
        this.currentConfiguration = this.configurationYaml;
    }

    onTabChanged(event: MatTabChangeEvent) {
        if (event.index === 0) {
            this.currentConfiguration = this.configurationYaml;
        } else {
            this.currentConfiguration = this.configurationJson;
        }
    }
}
