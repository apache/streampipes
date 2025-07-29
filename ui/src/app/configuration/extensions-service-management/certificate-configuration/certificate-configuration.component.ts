import { Component, inject, OnInit } from '@angular/core';
import { CertificateService } from '../../../../../projects/streampipes/platform-services/src/lib/apis/certificate.service';
import { Certificate, CertificateState } from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';

@Component({
    selector: 'sp-certificate-configuration',
    standalone: false,
    templateUrl: './certificate-configuration.component.html',
    styleUrl: './certificate-configuration.component.scss',
})
export class CertificateConfigurationComponent implements OnInit {
    private certificateService = inject(CertificateService);

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
}
