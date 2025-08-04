import { Component, inject, OnInit } from '@angular/core';
import {
    Certificate,
    CertificateService,
    CertificateState,
} from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { CertificateDetailsDialogComponent } from '../../dialog/certificate-details/certificate-details-dialog.component';

@Component({
    selector: 'sp-certificate-configuration',
    standalone: false,
    templateUrl: './certificate-configuration.component.html',
    styleUrl: './certificate-configuration.component.scss',
})
export class CertificateConfigurationComponent implements OnInit {
    private certificateService = inject(CertificateService);
    private dialogService = inject(DialogService);

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
            title: 'Certificate details',
            panelType: PanelType.STANDARD_PANEL,
            width: '60vw',
            data: {
                certificate,
            },
        });
    }
}
