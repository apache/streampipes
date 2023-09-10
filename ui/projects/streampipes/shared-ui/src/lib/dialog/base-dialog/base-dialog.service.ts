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

import { ComponentType, Overlay, OverlayRef } from '@angular/cdk/overlay';
import { ComponentRef, Injectable, Injector } from '@angular/core';
import { DialogRef } from './dialog-ref';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';
import { BaseDialogComponent } from './base-dialog.component';
import {
    BaseDialogComponentUnion,
    DialogConfig,
    PanelType,
} from './base-dialog.model';
import { PanelDialogComponent } from '../panel-dialog/panel-dialog.component';
import { StandardDialogComponent } from '../standard-dialog/standard-dialog.component';
import { BaseDialogConfig } from './base-dialog.config';
import { PanelDialogConfig } from '../panel-dialog/panel-dialog.config';
import { StandardDialogConfig } from '../standard-dialog/standard-dialog.config';

@Injectable({
    providedIn: 'root',
})
export class DialogService {
    constructor(
        private overlay: Overlay,
        private injector: Injector,
    ) {}

    public open<T>(
        component: ComponentType<T>,
        config?: DialogConfig,
    ): DialogRef<T> {
        config = config || {
            width: '60vw',
            title: '',
            panelType: PanelType.SLIDE_IN_PANEL,
        };

        const positionStrategy = this.getPositionStrategy(config.panelType);
        const panelConfig: BaseDialogConfig = this.getConfig(config.panelType);
        const overlay = this.overlay.create(
            panelConfig.getOverlayConfig(config, positionStrategy),
        );

        const panelDialogContainer = new ComponentPortal(
            this.getPanel(config.panelType),
        );
        const panelDialogContainerRef = overlay.attach(panelDialogContainer);
        panelDialogContainerRef.instance.dialogTitle = config.title;
        const dialogRef = new DialogRef<T>(overlay, panelDialogContainerRef);

        const injector = this.createInjector(dialogRef);
        panelDialogContainerRef.instance.selectedPortal = new ComponentPortal(
            component,
            null,
            injector,
        );
        panelDialogContainerRef.instance.dialogRef = dialogRef;
        dialogRef.componentInstance = panelDialogContainerRef.instance.attach();

        if (config.data) {
            Object.keys(config.data).forEach(key => {
                dialogRef.componentInstance[key] = config.data[key];
            });
        }

        this.applyDialogProperties(panelDialogContainerRef, overlay, config);

        return dialogRef;
    }

    private createInjector<T, OUTER extends BaseDialogComponent<T>>(
        dialogRef: DialogRef<T>,
    ) {
        const injectorMap = new WeakMap();
        injectorMap.set(DialogRef, dialogRef);
        return new PortalInjector(this.injector, injectorMap);
    }

    private applyDialogProperties(
        panelDialogComponentRef: ComponentRef<any>,
        overlayRef: OverlayRef,
        config: DialogConfig,
    ) {
        panelDialogComponentRef.instance.containerEvent.subscribe(e => {
            if (e.key === 'CLOSE') {
                overlayRef.dispose();
            }
        });
        if (!config.disableClose) {
            overlayRef.backdropClick().subscribe(() => overlayRef.dispose());
        }
    }

    getPositionStrategy(panelType: PanelType) {
        return this.getConfig(panelType).getPosition(this.overlay);
    }

    getPanel(panelType: PanelType): ComponentType<BaseDialogComponentUnion> {
        return panelType === PanelType.SLIDE_IN_PANEL
            ? PanelDialogComponent
            : StandardDialogComponent;
    }

    getConfig(panelType: PanelType): BaseDialogConfig {
        return panelType === PanelType.SLIDE_IN_PANEL
            ? new PanelDialogConfig()
            : new StandardDialogConfig();
    }
}
