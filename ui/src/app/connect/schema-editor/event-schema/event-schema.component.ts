/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {RestService} from '../../rest.service';
import {EventSchema} from '../model/EventSchema';
import {AdapterDescription} from '../../model/connect/AdapterDescription';
import {GuessSchema} from '../model/GuessSchema';
import {NotificationLd} from '../../model/message/NotificationLd';
import {EventProperty} from '../model/EventProperty';
import {EventPropertyNested} from '../model/EventPropertyNested';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';
import {ITreeOptions, TreeComponent} from 'angular-tree-component';
import {UUID} from 'angular2-uuid';
import {DataTypesService} from '../data-type.service';

@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css']
})
export class EventSchemaComponent implements OnChanges {

  constructor(private restService: RestService, private dataTypesService: DataTypesService) { }

  @Input() adapterDescription: AdapterDescription;
  @Input() isEditable = true;
  @Input() oldEventSchema: EventSchema;
  @Input() eventSchema: EventSchema = new EventSchema();

  @Output() isEditableChange = new EventEmitter<boolean>();
  @Output() adapterChange = new EventEmitter<AdapterDescription>();
  @Output() eventSchemaChange = new EventEmitter<EventSchema>();
  @Output() oldEventSchemaChange = new EventEmitter<EventSchema>();

  @ViewChild(TreeComponent, { static: true }) tree: TreeComponent;

  schemaGuess: GuessSchema = new GuessSchema();
  countSelected: number = 0;
  isLoading = false;
  isError = false;
  isPreviewEnabled = false;
  showErrorMessage = false;
  errorMessages: NotificationLd[];
  nodes: EventProperty[] = new Array<EventProperty>();
  options: ITreeOptions = {
    childrenField: 'eventProperties',
    allowDrag: () => {
      return this.isEditable;
    },
    allowDrop: (node, { parent, index }) => {
      return parent.data.eventProperties !== undefined && parent.parent !== null;
    },
    displayField: 'runTimeName',
  };


  public onUpdateData(treeComponent: TreeComponent): void {
    treeComponent.treeModel.expandAll();
  }

  public guessSchema(): void {
    this.isLoading = true;
    this.isError = false;
    this.restService.getGuessSchema(this.adapterDescription).subscribe(guessSchema => {
      this.isLoading = false;
      this.eventSchema = guessSchema.eventSchema;
      this.eventSchema.eventProperties.sort((a, b) => {
        return a.runtimeName < b.runtimeName ? -1 : 1;
      });
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

  private refreshTree(): void {
    this.nodes = new Array<EventProperty>();
    this.nodes.push(this.eventSchema as unknown as EventProperty);
    this.tree.treeModel.update();
  }

  public addNestedProperty(eventProperty?: EventPropertyNested): void {
    const uuid: string = UUID.UUID();
    if (eventProperty === undefined) {
      this.eventSchema.eventProperties.push(new EventPropertyNested(uuid, undefined));
    } else {
      eventProperty.eventProperties.push(new EventPropertyNested(uuid, undefined));
    }
    this.refreshTree();
  }


  public removeSelectedProperties(eventProperties?: any): void {
    eventProperties = eventProperties || this.eventSchema.eventProperties;
    for (let i = eventProperties.length - 1; i >= 0; --i) {
      if (eventProperties[i].eventProperties) {
        this.removeSelectedProperties(eventProperties[i].eventProperties);
      }
      if (eventProperties[i].selected) {
        eventProperties.splice(i, 1);
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

  public togglePreview(): void {
    this.isPreviewEnabled = !this.isPreviewEnabled;
  }

  ngOnChanges(changes: SimpleChanges) {
    setTimeout(() => { this.refreshTree() }, 200);
  }
}
