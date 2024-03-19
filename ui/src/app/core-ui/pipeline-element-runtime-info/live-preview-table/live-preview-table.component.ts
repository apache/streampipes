import { Component, Input } from '@angular/core';
import { EventSchema } from '@streampipes/platform-services';
import { SemanticTypeUtilsService } from '../../../core-services/semantic-type/semantic-type-utils.service';

@Component({
    selector: 'sp-live-preview-table',
    templateUrl: './live-preview-table.component.html',
    styleUrls: ['./live-preview-table.component.scss'],
})
export class LivePreviewTableComponent {
    @Input()
    eventSchema: EventSchema;

    @Input()
    runtimeData: { runtimeName: string; value: any }[];

    displayedColumns: string[] = ['runtimeName', 'value'];

    constructor(private semanticTypeUtilsService: SemanticTypeUtilsService) {}

    isImage(runtimeName: string) {
        const property = this.getProperty(runtimeName);
        return this.semanticTypeUtilsService.isImage(property);
    }

    isTimestamp(runtimeName: string) {
        const property = this.getProperty(runtimeName);
        return this.semanticTypeUtilsService.isTimestamp(property);
    }

    hasNoDomainProperty(runtimeName: string) {
        return !(this.isTimestamp(runtimeName) || this.isImage(runtimeName));
    }

    getProperty(runtimeName: string) {
        return this.eventSchema.eventProperties.find(
            property => property.runtimeName === runtimeName,
        );
    }
}
