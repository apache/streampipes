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


  @Input()
  isEditable: Boolean;
  @Output()
  isEditableChange = new EventEmitter<Boolean>();

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
  public isError: boolean = false;
  public showErrorMessage: boolean = false;
  public errorMessage: string;

  constructor(
    private restService: RestService,
  ) {}

  public guessSchema(): void {
    this.isLoading = true;
    this.isError = false;
        this.restService.getGuessSchema(this.adapterDescription).subscribe(x => {
                this.isLoading = false;
                this.eventSchema = x.eventSchema;
                this.eventSchemaChange.emit(this.eventSchema);
                this.schemaGuess = x;

                this.oldEventSchema = this.eventSchema.copy();
                this.oldEventSchemaChange.emit(this.oldEventSchema);

                this.isEditable = true;
                this.isEditableChange.emit(true);
            },
            error => {
                this.errorMessage = error.error;
                this.isError = true;
                this.isLoading = false;
                this.eventSchema = new EventSchema();
            });

  }

  ngOnInit() {
      // this.guessSchema();
      if (!this.eventSchema) {
        this.eventSchema = new EventSchema();
      }

  }
}
