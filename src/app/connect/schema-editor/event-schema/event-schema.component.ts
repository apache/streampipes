import { Component, Input, EventEmitter, OnInit, Output, ViewChild, ChangeDetectorRef } from '@angular/core';
import { RestService } from '../../rest.service';
import { EventSchema } from '../model/EventSchema';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { GuessSchema } from '../model/GuessSchema';
import { NotificationLd } from '../../model/message/NotificationLd';
import { FlatTreeControl, CdkTree } from '@angular/cdk/tree';
import { ExampleNode } from '../model/ExampleNode';
import { ArrayDataSource } from '@angular/cdk/collections';
import { EventProperty } from '../model/EventProperty';
import { EventPropertyNested } from '../model/EventPropertyNested';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { EventPropertyList } from '../model/EventPropertyList';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { ITreeOptions, TreeComponent } from 'angular-tree-component';
import { DomainPropertyProbabilityList } from '../model/DomainPropertyProbabilityList';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css'],
})
export class EventSchemaComponent implements OnInit {

  constructor(private restService: RestService) {}
  @Input() adapterDescription: AdapterDescription;
  @Input() isEditable: boolean;
  @Output() isEditableChange = new EventEmitter<boolean>();

  @Output() adapterChange = new EventEmitter<AdapterDescription>();

  @Input() eventSchema: EventSchema;
  @Output() eventSchemaChange = new EventEmitter<EventSchema>();

  @Input() oldEventSchema: EventSchema;
  @Output() oldEventSchemaChange = new EventEmitter<EventSchema>();

  @Input() domainPropertyGuesses: DomainPropertyProbabilityList[] = [];

  schemaGuess: GuessSchema = new GuessSchema();

  nodes: EventProperty[] = new Array<EventProperty>();
  options: ITreeOptions = {
    childrenField: 'eventProperties',
    allowDrag: (node) => {
      return true;
    },
    allowDrop: (node) => {
      return true;
    },
    displayField: 'runTimeName',
  };
  @ViewChild(TreeComponent)
  private tree: TreeComponent;

  isLoading = false;
  isError = false;
  showErrorMessage = false;
  errorMessages: NotificationLd[];

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

        this.nodes = new Array<EventProperty>();
        this.nodes.push(this.eventSchema as unknown as EventProperty);
        this.tree.treeModel.update();
        this.tree.treeModel.expandAll();

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

  private isEventPropertyPrimitive(instance): boolean {
    return instance instanceof EventPropertyPrimitive;
  }

  private isEventPropertyNested(instance): boolean {
    return instance instanceof EventPropertyNested;
  }

  private isEventPropertyList(instance): boolean {
    return instance instanceof EventPropertyList;
  }

  public getDomainProbability(name: string) {
    let result: DomainPropertyProbabilityList;

    for (const entry of this.domainPropertyGuesses) {
        if (entry.runtimeName === name) {
            result = entry;
        }
    }

    return result;
  }

  ngOnInit() {
    if (!this.eventSchema) {
      this.eventSchema = new EventSchema();
    }
  }
}
