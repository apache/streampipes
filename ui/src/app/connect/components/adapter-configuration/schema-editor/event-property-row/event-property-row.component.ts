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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UUID } from 'angular2-uuid';
import { TreeNode } from '@circlon/angular-tree-component';
import { MatDialog } from '@angular/material/dialog';
import {
    EventProperty,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    EventSchema,
    FieldStatusInfo,
} from '@streampipes/platform-services';
import { EditEventPropertyComponent } from '../../../../dialog/edit-event-property/edit-event-property.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-event-property-row',
    templateUrl: './event-property-row.component.html',
    styleUrls: ['./event-property-row.component.scss'],
})
export class EventPropertyRowComponent implements OnInit {
    @Input() node: TreeNode;
    @Input() isEditable = true;
    @Input() eventSchema: EventSchema = new EventSchema();
    @Input() originalEventSchema: EventSchema;
    @Input() countSelected: number;
    @Input() fieldStatusInfo: Record<string, FieldStatusInfo>;

    @Output() isEditableChange = new EventEmitter<boolean>();
    @Output() eventSchemaChange = new EventEmitter<EventSchema>();
    @Output() originalEventSchemaChange = new EventEmitter<EventSchema>();
    @Output() refreshTreeEmitter = new EventEmitter<boolean>();
    @Output() countSelectedChange = new EventEmitter<number>();

    label: string;
    isPrimitive = false;
    isNested = false;
    isList = false;
    timestampProperty = false;
    showFieldStatus = false;

    runtimeType: string;
    originalRuntimeType: string;
    originalRuntimeName: string;

    constructor(
        private dialog: MatDialog,
        private dialogService: DialogService,
    ) {}

    ngOnInit() {
        this.label = this.getLabel(this.node.data);
        this.isPrimitive = this.isEventPropertyPrimitive(this.node.data);
        this.isList = this.isEventPropertyList(this.node.data);
        this.isNested = this.isEventPropertyNested(this.node.data);
        this.timestampProperty = this.isTimestampProperty(this.node.data);

        if (this.node.data instanceof EventProperty) {
            const originalProperty = this.findOriginalProperty(
                this.originalEventSchema.eventProperties,
            );
            if (originalProperty) {
                this.originalRuntimeName = originalProperty.runtimeName;
                this.showFieldStatus =
                    this.fieldStatusInfo &&
                    this.fieldStatusInfo[this.originalRuntimeName] !==
                        undefined;
                if (this.isPrimitive) {
                    this.originalRuntimeType = this.parseType(
                        originalProperty.runtimeType,
                    );
                    this.runtimeType = this.parseType(
                        (this.node.data as EventPropertyPrimitive).runtimeType,
                    );
                }
            }
        }

        if (!this.node.data.propertyScope) {
            this.node.data.propertyScope = 'MEASUREMENT_PROPERTY';
        }
    }

    private findOriginalProperty(properties: EventPropertyUnion[]): any {
        let result: EventPropertyUnion | undefined;

        for (const property of properties) {
            if (property.elementId === this.node.data.elementId) {
                result = property;
                break;
            } else if (property instanceof EventPropertyNested) {
                result = this.findOriginalProperty(property.eventProperties);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    private parseType(runtimeType: string) {
        return runtimeType.split('#')[1].toUpperCase();
    }

    private isEventPropertyPrimitive(instance: EventProperty): boolean {
        return instance instanceof EventPropertyPrimitive;
    }

    private isEventPropertyNested(instance: EventProperty): boolean {
        return instance instanceof EventPropertyNested;
    }

    private isEventPropertyList(instance: EventProperty): boolean {
        return instance instanceof EventPropertyList;
    }

    public getLabel(eventProperty: EventProperty) {
        if (eventProperty.label && eventProperty.label !== '') {
            return eventProperty.label;
        } else if (
            eventProperty.runtimeName !== undefined &&
            eventProperty.runtimeName !== ''
        ) {
            return eventProperty.runtimeName;
        }
        if (this.isEventPropertyNested(eventProperty)) {
            return 'Nested Property';
        }
        if (eventProperty instanceof EventSchema) {
            return '';
        }
        return 'Property';
    }

    isTimestampProperty(node) {
        if (
            node.domainProperties &&
            node.domainProperties.some(
                dp => dp === 'http://schema.org/DateTime',
            )
        ) {
            node.runtimeType = 'http://www.w3.org/2001/XMLSchema#long';
            return true;
        } else {
            return false;
        }
    }

    public openEditDialog(data): void {
        const dialogRef = this.dialogService.open(EditEventPropertyComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Edit field ' + data.runtimeName,
            width: '50vw',
            data: {
                property: data,
                isEditable: this.isEditable,
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            this.timestampProperty = this.isTimestampProperty(this.node.data);
            this.refreshTreeEmitter.emit(true);
        });
    }

    public selectProperty(id: string, eventProperties: any): void {
        if (!this.isEditable) {
            return;
        }
        eventProperties = eventProperties || this.eventSchema.eventProperties;
        for (const eventProperty of eventProperties) {
            if (
                eventProperty.eventProperties &&
                eventProperty.eventProperties.length > 0
            ) {
                if (eventProperty.id === id) {
                    if (eventProperty.selected) {
                        eventProperty.selected = undefined;
                        this.countSelected--;
                        this.selectProperty(
                            'none',
                            eventProperty.eventProperties,
                        );
                    } else {
                        eventProperty.selected = true;
                        this.countSelected++;
                        this.selectProperty(
                            'all',
                            eventProperty.eventProperties,
                        );
                    }
                } else if (id === 'all') {
                    eventProperty.selected = true;
                    this.countSelected++;
                    this.selectProperty('all', eventProperty.eventProperties);
                } else if (id === 'none') {
                    eventProperty.selected = undefined;
                    this.countSelected--;
                    this.selectProperty('none', eventProperty.eventProperties);
                } else {
                    this.selectProperty(id, eventProperty.eventProperties);
                }
            } else {
                if (eventProperty.id === id) {
                    if (eventProperty.selected) {
                        eventProperty.selected = undefined;
                        this.countSelected--;
                    } else {
                        eventProperty.selected = true;
                        this.countSelected++;
                    }
                } else if (id === 'all') {
                    eventProperty.selected = true;
                    this.countSelected++;
                } else if (id === 'none') {
                    eventProperty.selected = undefined;
                    this.countSelected--;
                }
            }
        }
        this.countSelectedChange.emit(this.countSelected);
        this.refreshTreeEmitter.emit(false);
    }

    public addNestedProperty(eventProperty: EventPropertyNested): void {
        const uuid: string = UUID.UUID();
        if (!eventProperty.eventProperties) {
            eventProperty.eventProperties = new Array<EventPropertyUnion>();
        }
        const property: EventPropertyNested = new EventPropertyNested();
        property.elementId = uuid;
        eventProperty.eventProperties.push(property);
        this.refreshTreeEmitter.emit(false);
    }
}
