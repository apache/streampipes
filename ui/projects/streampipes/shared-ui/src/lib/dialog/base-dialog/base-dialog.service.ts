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
import { ComponentRef, Injectable, Injector, inject } from '@angular/core';
import { DialogRef } from './dialog-ref';
import { ComponentPortal } from '@angular/cdk/portal';
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
import { CardDialogComponent } from '../card-dialog/card-dialog.component';
import { CardDialogConfig } from '../card-dialog/card-dialog-config';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
    providedIn: 'root',
})
export class DialogService {
    private openDialogs: DialogRef<any>[] = [];
    private matDialog = inject(MatDialog);

    private overlay = inject(Overlay);
    private injector = inject(Injector);

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
        const panelConfig = this.getConfig(config.panelType);
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
            Object.keys(config.data).forEach(
                key => (dialogRef.componentInstance[key] = config.data[key]),
            );
        }

        this.applyDialogProperties(panelDialogContainerRef, overlay, config);

        this.openDialogs.push(dialogRef);
        dialogRef
            .afterClosed()
            .subscribe(
                () =>
                    (this.openDialogs = this.openDialogs.filter(
                        d => d !== dialogRef,
                    )),
            );

        return dialogRef;
    }

    get hasOpenDialogs() {
        return (
            this.openDialogs.length > 0 || this.matDialog.openDialogs.length > 0
        );
    }

    private createInjector<T>(dialogRef: DialogRef<T>): Injector {
        return Injector.create({
            parent: this.injector,
            providers: [{ provide: DialogRef, useValue: dialogRef }],
        });
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

        overlayRef.keydownEvents().subscribe(e => {
            if (e.key === 'Escape' && !config.disableClose) {
                panelDialogComponentRef.instance.close();
            }
        });

        if (!config.disableClose) {
            overlayRef.backdropClick().subscribe(() => {
                panelDialogComponentRef.instance.close();
                overlayRef.dispose();
            });
        }
    }

    getPositionStrategy(panelType: PanelType) {
        return this.getConfig(panelType).getPosition(this.overlay);
    }

    getPanel(panelType: PanelType): ComponentType<BaseDialogComponentUnion> {
        if (panelType === PanelType.STANDARD_PANEL) {
            return StandardDialogComponent;
        } else if (panelType === PanelType.CARD) {
            return CardDialogComponent;
        } else {
            return PanelDialogComponent;
        }
    }

    getConfig(panelType: PanelType): BaseDialogConfig {
        if (panelType === PanelType.STANDARD_PANEL) {
            return new StandardDialogConfig();
        } else if (panelType === PanelType.CARD) {
            return new CardDialogConfig();
        } else {
            return new PanelDialogConfig();
        }
    }
}
