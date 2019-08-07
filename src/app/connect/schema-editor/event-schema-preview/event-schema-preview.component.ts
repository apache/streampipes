import { Component, Input } from "@angular/core";
import { EventSchema } from "../model/EventSchema";

@Component({
    selector: 'app-event-schema-preview',
    templateUrl: './event-schema-preview.component.html',
    styleUrls: ['./event-schema-preview.component.css']
})
export class EventSchemaPreviewComponent {
    @Input() eventSchema: EventSchema;
}