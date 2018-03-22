import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';
import {DragulaService} from 'ng2-dragula/ng2-dragula';
import {RestService} from '../../rest.service';
import {EventSchema} from '../model/EventSchema';
import {AdapterDescription} from '../../model/AdapterDescription';
import {ProtocolDescription} from '../../model/ProtocolDescription';
import {FormatDescription} from '../../model/FormatDescription';
import {EventProperty} from '../model/EventProperty';

@Component({
    selector: 'app-event-schema',
    templateUrl: './event-schema.component.html',
    styleUrls: ['./event-schema.component.css']
})

export class EventSchemaComponent implements OnInit {

    @Input() protocol: ProtocolDescription;
    @Input() format: FormatDescription;

    @Output() adapterChange = new EventEmitter<AdapterDescription>();

    public eventSchema: EventSchema = null;

    constructor(private restService: RestService,
                private dragulaService: DragulaService) {
    }


    public guessSchema(): void {
        const adapter = new AdapterDescription('http://bb.de');
        adapter.protocol = this.protocol;
        adapter.format = this.format;

        // let self = this;

        this.restService.getGuessSchema(adapter).subscribe(x => {
            this.eventSchema = x;
            // this.eventProperties = x.eventProperties;
            console.log(x);
            console.log(this.eventSchema);
        });
    }

    ngOnInit() {
        this.eventSchema = new EventSchema();
    }



}
