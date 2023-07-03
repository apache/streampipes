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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddService } from '../../services/add.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {
    ExtensionsServiceEndpointItem,
    PipelineElementEndpointService,
} from '@streampipes/platform-services';
import { AppConstants } from '../../../services/app.constants';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { PanelType, DialogService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-endpoint-item',
    templateUrl: './endpoint-item.component.html',
    styleUrls: ['./endpoint-item.component.scss'],
})
export class EndpointItemComponent implements OnInit {
    @Input()
    item: ExtensionsServiceEndpointItem;

    itemTypeTitle: string;
    itemTypeStyle: string;

    @Input()
    itemSelected: boolean;

    image: SafeUrl;
    iconReady = false;
    iconError = false;

    @Output()
    triggerInstallation: EventEmitter<any> = new EventEmitter<any>();

    constructor(
        private snackBar: MatSnackBar,
        private pipelineElementEndpointService: PipelineElementEndpointService,
        private addService: AddService,
        private sanitizer: DomSanitizer,
        public appConstants: AppConstants,
        private dialogService: DialogService,
    ) {}

    ngOnInit(): void {
        this.findItemTypeTitle();
        this.findItemStyle();
        if (this.item.includesIcon) {
            this.addService.getRdfEndpointIcon(this.item).subscribe(
                blob => {
                    const objectURL = URL.createObjectURL(blob);
                    this.image =
                        this.sanitizer.bypassSecurityTrustUrl(objectURL);
                    this.iconReady = true;
                },
                () => (this.iconError = true),
            );
        }
    }

    iconText(s) {
        let result = '';
        if (s.length <= 4) {
            result = s;
        } else {
            const words = s.split(' ');
            words.forEach((word, i) => {
                if (i < 4) {
                    result += word.charAt(0);
                }
            });
        }
        return result.toUpperCase();
    }

    getSelectedBackground() {
        if (this.itemSelected) {
            return 'var(--color-bg-2)';
        } else {
            return 'var(--color-bg-1)';
        }
    }

    findItemTypeTitle() {
        if (this.item.type === 'stream') {
            this.itemTypeTitle = 'Data Stream';
        } else if (this.item.type === 'sepa') {
            this.itemTypeTitle = 'Data Processor';
        } else {
            this.itemTypeTitle = 'Data Sink';
        }
    }

    findItemStyle() {
        const baseType = 'pe-label ';
        if (this.item.type === 'stream') {
            this.itemTypeStyle = baseType + 'stream-label';
        } else if (this.item.type === 'set') {
            this.itemTypeStyle = baseType + 'set-label';
        } else if (this.item.type === 'sepa') {
            this.itemTypeStyle = baseType + 'processor-label';
        } else {
            this.itemTypeStyle = baseType + 'sink-label';
        }
    }

    installSingleElement(event: Event, endpointItem) {
        const endpointItems = [];
        endpointItems.push(endpointItem);
        this.triggerInstallation.emit({ endpointItems, install: true });
        event.stopPropagation();
    }

    uninstallSingleElement(event: Event, endpointItem) {
        const endpointItems = [];
        endpointItems.push(endpointItem);
        this.triggerInstallation.emit({ endpointItems, install: false });
        event.stopPropagation();
    }

    refresh(elementId: string) {
        this.pipelineElementEndpointService
            .update(elementId)
            .subscribe(msg => {
                this.snackBar.open(msg.notifications[0].title, 'Ok', {
                    duration: 2000,
                });
            })
            .add(() => {
                // this.loadCurrentElements(type);
            });
    }

    showPermissionsDialog(elementId: string, elementName: string) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage permissions',
                width: '50vw',
                data: {
                    objectInstanceId: elementId,
                    headerTitle:
                        'Manage permissions for pipeline element ' +
                        elementName,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            console.log(refresh);
        });
    }
}
