import { Component, Input, EventEmitter, OnInit, Output, ViewChild, ChangeDetectorRef, Pipe, PipeTransform } from '@angular/core';
import { RestService } from '../../rest.service';
import { EventSchema } from '../model/EventSchema';
import { AdapterDescription } from '../../model/connect/AdapterDescription';
import { GuessSchema } from '../model/GuessSchema';
import { NotificationLd } from '../../model/message/NotificationLd';
import { EventProperty } from '../model/EventProperty';
import { EventPropertyNested } from '../model/EventPropertyNested';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { EventPropertyList } from '../model/EventPropertyList';
import { ITreeOptions, TreeComponent } from 'angular-tree-component';
import { DomainPropertyProbabilityList } from '../model/DomainPropertyProbabilityList';
import { UUID } from 'angular2-uuid';
import { DataTypesService } from '../data-type.service';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css']
})
export class EventSchemaComponent implements OnInit {

  constructor(private restService: RestService, private dataTypesService: DataTypesService) { }
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
    allowDrop: (node, { parent, index }) => {
      return parent.data.eventProperties !== undefined && parent.parent !== null;
    },
    displayField: 'runTimeName',
  };
  @ViewChild(TreeComponent)
  private tree: TreeComponent;

  isLoading = false;
  isError = false;
  isPreviewEnabled = false;
  showErrorMessage = false;
  countSelected = 0;
  errorMessages: NotificationLd[];

  onUpdateData(treeComponent: TreeComponent, $event) {
    treeComponent.treeModel.expandAll();
  }

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

      this.refreshTree();

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

  public showPreview() {
    this.isPreviewEnabled = !this.isPreviewEnabled;
  }

  private refreshTree() {
    this.nodes = new Array<EventProperty>();
    this.nodes.push(this.eventSchema as unknown as EventProperty);
    this.tree.treeModel.update();
  }

  private isTopLevelProperty(instance): boolean {
    return this.eventSchema.eventProperties.indexOf(instance) !== -1;
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

  public changePropertyScope(eventProperty) {
    if (eventProperty.propertyScope == 'MEASUREMENT_PROPERTY') {
      eventProperty.propertyScope = 'DIMENSION_PROPERTY';
    } else {
      eventProperty.propertyScope = 'MEASUREMENT_PROPERTY';
    }
  }

  private isNested(property) {
    if (property.eventProperties !== undefined && !(property instanceof EventSchema)) {
      return true;
    }
    return false;
  }

  public selectProperty(id, eventProperties) {
    eventProperties = eventProperties || this.eventSchema.eventProperties
    for (const eventProperty of eventProperties) {
      if (eventProperty.eventProperties && eventProperty.eventProperties.length > 0) {
        if (eventProperty.id == id) {
          if (eventProperty.selected) {
            eventProperty.selected = undefined;
            this.countSelected--;
            this.selectProperty('none', eventProperty.eventProperties)
          } else {
            eventProperty.selected = true;
            this.countSelected++;
            this.selectProperty('all', eventProperty.eventProperties)
          }
        } else if (id == 'all') {
          eventProperty.selected = true;
          this.countSelected++;
        } else if (id == 'none') {
          eventProperty.selected = undefined;
          this.countSelected--;
        } else {
          this.selectProperty(id, eventProperty.eventProperties)
        }
      } else {
        if (eventProperty.id == id) {
          if (eventProperty.selected) {
            eventProperty.selected = undefined;
            this.countSelected--;
          } else {
            eventProperty.selected = true;
            this.countSelected++;
          }
        } else if (id == 'all') {
          eventProperty.selected = true;
          this.countSelected++;
        } else if (id == 'none') {
          eventProperty.selected = undefined;
          this.countSelected--;
        }
      }
    }
    this.refreshTree();
  }

  public removeSelectedProperties(eventProperties) {
    eventProperties = eventProperties || this.eventSchema.eventProperties
    for (const eventProperty of eventProperties) {
      if (eventProperty.selected) {
        const index = eventProperties.indexOf(eventProperty);
        eventProperties.splice(index, 1)
      }
      if (eventProperty.eventProperties) {
        this.removeSelectedProperties(eventProperty.eventProperties)
      }
    }
    this.countSelected = 0;
    this.refreshTree();
  }

  public addStaticValueProperty(): void {
    const eventProperty = new EventPropertyPrimitive('staticValue/' + UUID.UUID(), undefined);

    eventProperty.setRuntimeName('key_0');
    eventProperty.setRuntimeType(this.dataTypesService.getStringTypeUrl());

    this.eventSchema.eventProperties.push(eventProperty);
    this.refreshTree();
  }

  public addTimestampProperty(): void {
    const eventProperty = new EventPropertyPrimitive('timestamp/' + UUID.UUID(), undefined);

    eventProperty.setRuntimeName('timestamp');
    eventProperty.setLabel('Timestamp');
    eventProperty.setDomainProperty('http://schema.org/DateTime');
    eventProperty.setRuntimeType(this.dataTypesService.getNumberTypeUrl());

    this.eventSchema.eventProperties.push(eventProperty);
    this.refreshTree();
  }

  public addNestedProperty(eventProperty): void {
    const uuid: string = UUID.UUID();
    if (eventProperty == undefined) {
      this.eventSchema.eventProperties.push(new EventPropertyNested(uuid, undefined));
    } else {
      eventProperty.eventProperties.push(new EventPropertyNested(uuid, undefined));
    }
    this.refreshTree();
  }

  ngOnInit() {
    if (!this.eventSchema) {
      this.eventSchema = new EventSchema();
    }
  }
}
