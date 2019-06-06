import { Component, Input, EventEmitter, OnInit, Output } from '@angular/core';
import { RestService } from '../../rest.service';
import { EventSchema } from '../model/EventSchema';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { GuessSchema } from '../model/GuessSchema';
import { NotificationLd } from '../../model/message/NotificationLd';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css'],
})
export class EventSchemaComponent implements OnInit {
  @Input() adapterDescription: AdapterDescription;
  @Input() isEditable: boolean;
  @Output() isEditableChange = new EventEmitter<boolean>();

  @Output() adapterChange = new EventEmitter<AdapterDescription>();

  @Input() eventSchema: EventSchema;
  @Output() eventSchemaChange = new EventEmitter<EventSchema>();

  @Input() oldEventSchema: EventSchema;
  @Output() oldEventSchemaChange = new EventEmitter<EventSchema>();

  schemaGuess: GuessSchema = new GuessSchema();

  isLoading = false;
  isError = false;
  showErrorMessage = false;
  errorMessages: NotificationLd[];

  constructor(private restService: RestService) {}

  public guessSchema(): void {
    this.isLoading = true;
    this.isError = false;
    this.restService.getGuessSchema(this.adapterDescription).subscribe(guessSchema => {
        this.isLoading = false;
        this.eventSchema = guessSchema.eventSchema;
        this.eventSchemaChange.emit(this.eventSchema);
        this.schemaGuess = guessSchema;

        this.oldEventSchema = this.eventSchema.copy();
        this.oldEventSchemaChange.emit(this.oldEventSchema);

        this.isEditable = true;
        this.isEditableChange.emit(true);
    },
    error => {
        this.errorMessages = error.notifications;
        this.isError = true;
        this.isLoading = false;
        this.eventSchema = new EventSchema();
    });

  }

  ngOnInit() {
    if (!this.eventSchema) {
      this.eventSchema = new EventSchema();
    }

  }
}
