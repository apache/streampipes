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
import { ConfigurationService } from '../shared/configuration.service';
import {
    CdkDrag,
    CdkDragDrop,
    CdkDropList,
    moveItemInArray,
} from '@angular/cdk/drag-drop';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
    SplitSectionComponent,
    SpNavigationItem,
} from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';
import { MessagingSettings } from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { SpMessagingBrokerConfigComponent } from './broker-config/broker-config.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-messaging-configuration',
    templateUrl: './messaging-configuration.component.html',
    styleUrls: ['./messaging-configuration.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        SplitSectionComponent,
        FlexDirective,
        LayoutAlignDirective,
        FormsModule,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatButton,
        MatIcon,
        SpAlertBannerComponent,
        CdkDropList,
        CdkDrag,
        SpMessagingBrokerConfigComponent,
        MatLabel,
        TranslatePipe,
    ],
})
export class MessagingConfigurationComponent implements OnInit {
    tabs: SpNavigationItem[] = [];

    messagingSettings: MessagingSettings;
    loadingCompleted = false;

    constructor(
        private configurationService: ConfigurationService,
        private breadcrumbService: SpBreadcrumbService,
        private tabService: SpConfigurationTabsService,
    ) {}

    ngOnInit() {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('messaging') },
        ]);
        this.getMessagingSettings();
    }

    getMessagingSettings() {
        this.configurationService.getMessagingSettings().subscribe(response => {
            this.messagingSettings = response;
            this.loadingCompleted = true;
        });
    }

    updateMessagingSettings() {
        this.configurationService
            .updateMessagingSettings(this.messagingSettings)
            .subscribe(response => this.getMessagingSettings());
    }

    dropProtocol(event: CdkDragDrop<string[]>) {
        moveItemInArray(
            this.messagingSettings.prioritizedProtocols,
            event.previousIndex,
            event.currentIndex,
        );
    }
}
