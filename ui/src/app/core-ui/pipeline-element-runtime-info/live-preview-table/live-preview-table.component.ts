import { Component, Input } from '@angular/core';
import {
    EventPropertyUnion,
    EventSchema,
} from '@streampipes/platform-services';
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
    runtimeData: Record<string, any>;

    constructor(private semanticTypeUtilsService: SemanticTypeUtilsService) {}

    isImage(property: EventPropertyUnion) {
        return this.semanticTypeUtilsService.isImage(property);
    }

    isTimestamp(property: EventPropertyUnion) {
        return this.semanticTypeUtilsService.isTimestamp(property);
    }

    hasNoDomainProperty(property: EventPropertyUnion) {
        return !(this.isTimestamp(property) || this.isImage(property));
    }
}
