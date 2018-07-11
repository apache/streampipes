import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';
import {DragulaService} from 'ng2-dragula/ng2-dragula';
import {RestService} from '../../rest.service';
import {EventSchema} from '../model/EventSchema';
import {AdapterDescription} from '../../model/AdapterDescription';
import {ProtocolDescription} from '../../model/ProtocolDescription';
import {FormatDescription} from '../../model/FormatDescription';
import {EventProperty} from '../model/EventProperty';
import {GuessSchema} from '../model/GuessSchema';
import {AdapterSetDescription} from '../../model/AdapterSetDescription';
import {TransformationRuleService} from '../../transformation-rule.service';
import {TransformationRuleDescription} from '../../model/rules/TransformationRuleDescription';

@Component({
    selector: 'app-event-schema',
    templateUrl: './event-schema.component.html',
    styleUrls: ['./event-schema.component.css']
})

export class EventSchemaComponent implements OnInit {

    @Input() adapterDescription;
    @Output() adapterChange = new EventEmitter<AdapterDescription>();

    public eventSchema: EventSchema = null;

    public schemaGuess: GuessSchema = new GuessSchema();

    public isLoading: boolean = false;

    constructor(private restService: RestService,
                private dragulaService: DragulaService,
                private transformationRuleService: TransformationRuleService) {
    }


    public guessSchema(): void {
        this.isLoading = true;
        this.restService.getGuessSchema(this.adapterDescription).subscribe(x => {
            this.isLoading = false;
            this.eventSchema  = x.eventSchema;
            this.schemaGuess = x;

            this.transformationRuleService.setOldEventSchema(this.eventSchema);
        });
    }

    ngOnInit() {
        this.eventSchema = new EventSchema();
    }

    onNext() {
        if (this.adapterDescription.constructor.name == 'AdapterSetDescription') {
             this.adapterDescription.dataSet.eventSchema = this.eventSchema;
         } else {
             this.adapterDescription.dataStream.eventSchema = this.eventSchema;
         }

        this.transformationRuleService.setNewEventSchema(this.eventSchema);
        const transformationRules: TransformationRuleDescription[] = this.transformationRuleService.getTransformationRuleDescriptions();
        this.adapterDescription.rules = transformationRules;
    }



}
