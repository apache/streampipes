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

import { Component, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    ExtensionInstallationService,
    ExtensionItemDescription,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-extensions-installation-dialog',
    templateUrl: './extensions-installation.component.html',
    styleUrls: ['./extensions-installation.component.scss'],
})
export class SpExtensionsInstallationDialogComponent {
    endpointItems: ExtensionItemDescription[];

    @Input()
    install: boolean;

    @Input()
    endpointItemsToInstall: any;

    installedItemsChanged = false;

    installationStatus: any;
    installationFinished: boolean;
    page: string;
    nextButton: string;
    installationRunning: boolean;

    installAsPublicElement = true;

    constructor(
        private dialogRef: DialogRef<SpExtensionsInstallationDialogComponent>,
        private extensionInstallationService: ExtensionInstallationService,
    ) {
        this.installationStatus = [];
        this.installationFinished = false;
        this.page = 'preview';
        this.nextButton = 'Next';
        this.installationRunning = false;
    }

    close() {
        this.dialogRef.close(this.installedItemsChanged);
    }

    next() {
        if (this.page === 'installation') {
            this.close();
        } else {
            this.page = 'installation';
            this.initiateInstallation(this.endpointItemsToInstall[0], 0);
        }
    }

    initiateInstallation(
        extensionItem: ExtensionItemDescription,
        index: number,
    ) {
        this.installationRunning = true;
        this.installationStatus.push({
            name: extensionItem.name,
            id: index,
            status: 'waiting',
        });
        if (this.install) {
            this.installElement(extensionItem, index);
        } else {
            this.uninstallElement(extensionItem, index);
        }
    }

    installElement(extensionItem: ExtensionItemDescription, index: number) {
        this.extensionInstallationService
            .add({
                appId: extensionItem.appId || extensionItem.elementId,
                publicElement: this.installAsPublicElement,
                serviceTagPrefix: extensionItem.serviceTagPrefix,
            })
            .subscribe(
                data => {
                    if (data.success) {
                        this.installationStatus[index].status = 'success';
                    } else {
                        this.installationStatus[index].status = 'error';
                        this.installationStatus[index].details =
                            data.notifications[0].additionalInformation;
                    }
                },
                () => {
                    this.installationStatus[index].status = 'error';
                },
            )
            .add(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.endpointItemsToInstall[index],
                        index,
                    );
                } else {
                    this.installedItemsChanged = true;
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                }
            });
    }

    uninstallElement(extensionItem: ExtensionItemDescription, index: number) {
        this.extensionInstallationService
            .delete(extensionItem.elementId)
            .subscribe(
                data => {
                    this.installationStatus[index].status = data.success
                        ? 'success'
                        : 'error';
                },
                () => {
                    this.installationStatus[index].status = 'error';
                },
            )
            .add(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.endpointItemsToInstall[index],
                        index,
                    );
                } else {
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                    this.installedItemsChanged = true;
                }
            });
    }
}
