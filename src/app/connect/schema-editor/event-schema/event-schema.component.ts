import { Component, Input, EventEmitter, OnInit, Output } from '@angular/core';
import { RestService } from '../../rest.service';
import { EventSchema } from '../model/EventSchema';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { GuessSchema } from '../model/GuessSchema';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css'],
})
export class EventSchemaComponent implements OnInit {
  @Input()
  adapterDescription;
  @Output()
  adapterChange = new EventEmitter<AdapterDescription>();

  @Input()
  public eventSchema: EventSchema;
  @Output()
  eventSchemaChange = new EventEmitter<EventSchema>();

  @Input()
  public oldEventSchema: EventSchema;
  @Output()
  oldEventSchemaChange = new EventEmitter<EventSchema>();

  public schemaGuess: GuessSchema = new GuessSchema();

  public isLoading: boolean = false;

  constructor(
    private restService: RestService,
  ) {}

  public guessSchema(): void {
    this.isLoading = true;
    this.restService.getGuessSchema(this.adapterDescription).subscribe(x => {
      this.isLoading = false;
      this.eventSchema = x.eventSchema;
      this.eventSchemaChange.emit(this.eventSchema);
      this.schemaGuess = x;

      this.oldEventSchema = this.eventSchema.copy();
      this.oldEventSchemaChange.emit(this.oldEventSchema);
    });
  }

  ngOnInit() {

  }

  // onNext() {
  //   console.log('bla bla ');
  //   if (
  //     this.adapterDescription.constructor.name ==
  //       'GenericAdapterSetDescription' ||
  //     this.adapterDescription.constructor.name ==
  //       'SpecificAdapterSetDescription'
  //   ) {
  //     this.adapterDescription.dataSet.eventSchema = this.eventSchema;
  //   } else {
  //     this.adapterDescription.dataStream.eventSchema = this.eventSchema;
  //   }
  //
  //   this.transformationRuleService.setNewEventSchema(this.eventSchema);
  //   const transformationRules: TransformationRuleDescription[] = this.transformationRuleService.getTransformationRuleDescriptions();
  //   this.adapterDescription.rules = transformationRules;
  // }
}
