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
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@NgModule({
  declarations: [
    ConfirmDialogComponent,
    PanelDialogComponent,
    StandardDialogComponent,
    SpBasicViewComponent
  ],
  imports: [
    CommonModule,
    FlexLayoutModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    PortalModule,
    OverlayModule
  ],
  exports: [
    ConfirmDialogComponent, PanelDialogComponent, StandardDialogComponent, SpBasicViewComponent
  ]
})
export class SharedUiModule { }
