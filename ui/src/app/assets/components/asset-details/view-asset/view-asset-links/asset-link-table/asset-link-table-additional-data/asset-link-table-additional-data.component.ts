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
    inject,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { AssetLink, Certificate } from '@streampipes/platform-services';
import { CertificateDetailsDialogComponent } from '../../../../../../../core-ui/certificate-details/certificate-details-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-link-table-additional-data',
    templateUrl: './asset-link-table-additional-data.component.html',
    standalone: false,
})
export class AssetLinkTableAdditionalDataComponent implements OnChanges {
    @Input()
    assetLink: AssetLink;

    @Input()
    certificates: Certificate[] = [];

    @Input()
    isAdminUser = false;

    showCertificateInfo = false;
    matchingCertificates: Certificate[] = [];

    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);

    ngOnChanges(changes: SimpleChanges) {
        this.findAssociatedCertificates();
    }

    findAssociatedCertificates() {
        this.matchingCertificates = this.certificates.filter(
            c =>
                c.associatedResourceIds.find(
                    resourceId => resourceId === this.assetLink.resourceId,
                ) !== undefined,
        );
        this.showCertificateInfo = this.matchingCertificates.length > 0;
    }

    openCertificateDetailsDialog(): void {
        this.dialogService.open(CertificateDetailsDialogComponent, {
            title: this.translateService.instant('Certificate details'),
            panelType: PanelType.STANDARD_PANEL,
            width: '60vw',
            data: {
                certificate: this.matchingCertificates[0],
            },
        });
    }
}
