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

import { NgModule } from '@angular/core';
import { ConfirmDialogComponent } from './dialog/confirm-dialog/confirm-dialog.component';
import { PanelDialogComponent } from './dialog/panel-dialog/panel-dialog.component';
import { StandardDialogComponent } from './dialog/standard-dialog/standard-dialog.component';
import { CommonModule } from '@angular/common';
import { PortalModule } from '@angular/cdk/portal';
import { MatButtonModule } from '@angular/material/button';
import { OverlayModule } from '@angular/cdk/overlay';
import { SpBasicViewComponent } from './components/basic-view/basic-view.component';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SpBasicNavTabsComponent } from './components/basic-nav-tabs/basic-nav-tabs.component';
import { MatTabsModule } from '@angular/material/tabs';
import { SpBasicInnerPanelComponent } from './components/basic-inner-panel/basic-inner-panel.component';
import { SpBasicHeaderTitleComponent } from './components/basic-header-title/header-title.component';
import { SpExceptionMessageComponent } from './components/sp-exception-message/sp-exception-message.component';
import { SpExceptionDetailsDialogComponent } from './components/sp-exception-message/exception-details-dialog/exception-details-dialog.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogModule } from '@angular/material/dialog';
import { SplitSectionComponent } from './components/split-section/split-section.component';
import { SpLabelComponent } from './components/sp-label/sp-label.component';

@NgModule({
    declarations: [
        ConfirmDialogComponent,
        PanelDialogComponent,
        StandardDialogComponent,
        SpBasicInnerPanelComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpBasicNavTabsComponent,
        SpExceptionMessageComponent,
        SpExceptionDetailsDialogComponent,
        SpLabelComponent,
        SplitSectionComponent,
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatButtonModule,
        MatDividerModule,
        MatIconModule,
        MatTabsModule,
        MatTooltipModule,
        PortalModule,
        OverlayModule,
        MatDialogModule,
    ],
    exports: [
        ConfirmDialogComponent,
        PanelDialogComponent,
        StandardDialogComponent,
        SpBasicInnerPanelComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpBasicNavTabsComponent,
        SpExceptionMessageComponent,
        SpExceptionDetailsDialogComponent,
        SpLabelComponent,
        SplitSectionComponent,
    ],
})
export class SharedUiModule {}
