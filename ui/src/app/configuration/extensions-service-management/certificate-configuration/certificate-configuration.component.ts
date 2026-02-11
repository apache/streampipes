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

import { Component, inject, OnInit } from '@angular/core';
import {
    Certificate,
    CertificateService,
    CertificateState,
} from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    DialogService,
    PanelType,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { CertificateDetailsDialogComponent } from '../../../core-ui/certificate-details/certificate-details-dialog.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { CertificateLabelComponent } from './certificate-label/certificate-label.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-certificate-configuration',
    templateUrl: './certificate-configuration.component.html',
    styleUrls: ['./certificate-configuration.component.scss'],
    imports: [
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        FlexDirective,
        LayoutAlignDirective,
        MatSortHeader,
        MatCellDef,
        MatCell,
        CertificateLabelComponent,
        MatIconButton,
        MatTooltip,
        MatIcon,
        LayoutDirective,
        LayoutGapDirective,
        MatButton,
        TranslatePipe,
    ],
})
export class CertificateConfigurationComponent implements OnInit {
    private certificateService = inject(CertificateService);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);

    displayedColumns: string[] = ['issuer', 'expires', 'actions'];
    dataSource: MatTableDataSource<Certificate> =
        new MatTableDataSource<Certificate>();

    ngOnInit() {
        this.loadCertificates();
    }

    loadCertificates() {
        this.certificateService.getAllCertificates().subscribe(certs => {
            this.dataSource.data = certs;
        });
    }

    onStateChange(
        certificate: Certificate,
        certificateState: CertificateState,
    ) {
        certificate.state = certificateState;
        this.certificateService
            .updateCertificate(certificate)
            .subscribe(() => this.loadCertificates());
    }

    onDelete(certificate: Certificate) {
        this.certificateService
            .deleteCertificate(certificate.elementId)
            .subscribe(() => {
                this.loadCertificates();
            });
    }

    openDetailsDialog(certificate: Certificate): void {
        this.dialogService.open(CertificateDetailsDialogComponent, {
            title: this.translateService.instant('Certificate details'),
            panelType: PanelType.STANDARD_PANEL,
            width: '60vw',
            data: {
                certificate,
            },
        });
    }
}
