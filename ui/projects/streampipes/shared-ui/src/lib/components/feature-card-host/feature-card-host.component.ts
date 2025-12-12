import { FeatureCardRouteData } from './feature-card.model';
import { Component, inject, Input, OnInit, Type } from '@angular/core';
import { DialogRef } from '../../dialog/base-dialog/dialog-ref';

@Component({
    selector: 'sp-feature-card-host',
    templateUrl: './feature-card-host.component.html',
    styleUrls: ['./feature-card-host.component.scss'],
    standalone: false,
})
export class FeatureCardHostComponent implements OnInit {
    activeComponent: Type<any> | null = null;

    @Input()
    resourceId: string;

    @Input()
    card: FeatureCardRouteData;

    componentInputs: Record<string, any> | null = null;

    private dialogRef = inject(DialogRef<FeatureCardHostComponent>);

    async ngOnInit() {
        this.componentInputs = {
            resourceId: this.resourceId,
            onClose: () => this.close(),
        };
        this.activeComponent = await this.card.loadComponent();
    }

    close(): void {
        this.dialogRef.close();
    }
}
