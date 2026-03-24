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

import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    inject,
} from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {
    ExtensionInstallationService,
    ExtensionItemDescription,
    ExtensionItemInstallationRequest,
} from '@streampipes/platform-services';
import { AppConstants } from '../../../services/app.constants';
import {
    DialogService,
    ObjectPermissionDialogComponent,
    PanelType,
} from '@streampipes/shared-ui';
import { ExtensionsInstallationService } from '../extensions-installation.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-endpoint-item',
    templateUrl: './endpoint-item.component.html',
    styleUrls: ['./endpoint-item.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        NgClass,
        ClassDirective,
        MatTooltip,
        MatButton,
        MatMenuTrigger,
        MatMenu,
        MatMenuItem,
        MatIcon,
        TranslatePipe,
    ],
})
export class EndpointItemComponent implements OnInit {
    private snackBar = inject(MatSnackBar);
    private extensionInstallationService = inject(ExtensionInstallationService);
    private addService = inject(ExtensionsInstallationService);
    private sanitizer = inject(DomSanitizer);
    appConstants = inject(AppConstants);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);

    @Input()
    item: ExtensionItemDescription;

    itemTypeTitle: string;
    itemTypeStyle: string;
    itemTypeColor: string;

    @Input()
    itemSelected: boolean;

    image: SafeUrl;
    iconReady = false;
    iconError = false;

    cssMapper: Record<string, string> = {
        ADAPTER: 'adapter',
        DATA_STREAM: 'stream',
        DATA_PROCESSOR: 'sepa',
        DATA_SINK: 'action',
    };

    @Output()
    triggerInstallation: EventEmitter<any> = new EventEmitter<any>();

    ngOnInit(): void {
        this.findItemTypeTitle();
        this.findItemStyle();
        this.findItemColor();
        if (this.item.includesIcon) {
            this.addService.getExtensionItemIcon(this.item).subscribe(
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

    findItemTypeTitle() {
        if (this.item.serviceTagPrefix === 'ADAPTER') {
            this.itemTypeTitle = this.translateService.instant('Adapter');
        } else if (this.item.serviceTagPrefix === 'DATA_STREAM') {
            this.itemTypeTitle = this.translateService.instant('Data Stream');
        } else if (this.item.serviceTagPrefix === 'DATA_PROCESSOR') {
            this.itemTypeTitle =
                this.translateService.instant('Data Processor');
        } else {
            this.itemTypeTitle = this.translateService.instant('Data Sink');
        }
    }

    findItemStyle() {
        const baseType = 'pe-label ';
        if (this.item.serviceTagPrefix === 'DATA_STREAM') {
            this.itemTypeStyle = baseType + 'stream-label';
        } else if (this.item.serviceTagPrefix === 'ADAPTER') {
            this.itemTypeStyle = baseType + 'adapter-label';
        } else if (this.item.serviceTagPrefix === 'DATA_PROCESSOR') {
            this.itemTypeStyle = baseType + 'processor-label';
        } else {
            this.itemTypeStyle = baseType + 'sink-label';
        }
    }

    findItemColor() {
        if (this.item.serviceTagPrefix === 'ADAPTER') {
            this.itemTypeColor = 'var(--color-adapter)';
        } else if (this.item.serviceTagPrefix === 'DATA_STREAM') {
            this.itemTypeColor = 'var(--color-data-source)';
        } else if (this.item.serviceTagPrefix === 'DATA_PROCESSOR') {
            this.itemTypeColor = 'var(--color-processor)';
        } else if (this.item.serviceTagPrefix === 'DATA_SINK') {
            this.itemTypeColor = 'var(--color-sink)';
        } else {
            this.itemTypeColor = 'var(--color-sink)';
        }
    }

    installSingleElement(event: Event, endpointItem: ExtensionItemDescription) {
        const endpointItems = [];
        endpointItems.push(endpointItem);
        this.triggerInstallation.emit({ endpointItems, install: true });
        event.stopPropagation();
    }

    uninstallSingleElement(
        event: Event,
        endpointItem: ExtensionItemDescription,
    ) {
        const endpointItems = [];
        endpointItems.push(endpointItem);
        this.triggerInstallation.emit({ endpointItems, install: false });
        event.stopPropagation();
    }

    refresh(extensionItem: ExtensionItemDescription) {
        const installationReq: ExtensionItemInstallationRequest = {
            serviceTagPrefix: extensionItem.serviceTagPrefix,
            publicElement: false,
            appId: extensionItem.appId,
        };
        this.extensionInstallationService
            .update(installationReq)
            .subscribe(msg => {
                this.snackBar.open(msg.notifications[0].title, 'Ok', {
                    duration: 2000,
                });
            });
    }

    showPermissionsDialog(elementId: string, elementName: string) {
        this.dialogService.open(ObjectPermissionDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage permissions'),
            width: '50vw',
            data: {
                objectInstanceId: elementId,
                headerTitle: this.translateService.instant(
                    'Manage permissions for pipeline element {{name}}',
                    { name: elementName },
                ),
            },
        });
    }
}
