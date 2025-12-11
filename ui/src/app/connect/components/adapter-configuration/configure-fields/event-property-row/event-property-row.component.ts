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

import {
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    DataType,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventProperty,
    EventSchema,
    SemanticType,
    FieldStatusInfo,
} from '@streampipes/platform-services';
import { EditEventPropertyComponent } from '../../../../dialog/edit-event-property/edit-event-property.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { ShepherdService } from '../../../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-event-property-row',
    templateUrl: './event-property-row.component.html',
    styleUrls: ['./event-property-row.component.scss'],
    standalone: false,
})
export class EventPropertyRowComponent implements OnInit {
    private dialogService = inject(DialogService);
    private shepherdService = inject(ShepherdService);

    @Input() eventProperty: EventProperty;

    @Input() eventSchema: EventSchema = new EventSchema();
    @Input() originalEventSchema: EventSchema;
    @Input() fieldStatusInfo: Record<string, FieldStatusInfo>;

    @Output() eventSchemaChange = new EventEmitter<EventSchema>();
    @Output() refreshTreeEmitter = new EventEmitter<boolean>();

    label: string;

    isPrimitive = false;
    isNested = false;
    isList = false;

    timestampProperty = false;
    showFieldStatus = false;

    runtimeType: string;
    originalRuntimeType: string;
    originalRuntimeName: string;
    originalProperty: EventProperty;

    ngOnInit() {
        this.label = this.getLabel(this.eventProperty);
        this.isPrimitive = this.isEventPropertyPrimitive(this.eventProperty);
        this.isList = this.isEventPropertyList(this.eventProperty);
        this.isNested = this.isEventPropertyNested(this.eventProperty);
        this.timestampProperty = this.isTimestampProperty(this.eventProperty);

        this.originalRuntimeType = this.parseType(
            (this.eventProperty as EventPropertyPrimitive).runtimeType,
        );
        this.runtimeType = this.parseType(
            (this.eventProperty as EventPropertyPrimitive).runtimeType,
        );

        if (!this.eventProperty.propertyScope) {
            this.eventProperty.propertyScope = 'MEASUREMENT_PROPERTY';
        }
    }

    private checkAndDisplayProperties() {
        if (this.originalProperty) {
            this.applyDisplayedProperties(this.originalProperty);
        } else {
            this.applyDisplayedProperties(this.eventProperty);
        }
    }

    private applyDisplayedProperties(ep: EventProperty) {
        this.originalRuntimeName = ep.runtimeName;
        this.showFieldStatus =
            this.fieldStatusInfo &&
            this.fieldStatusInfo[this.originalRuntimeName] !== undefined;
        if (this.isPrimitive) {
            this.originalRuntimeType = this.parseType(
                (ep as EventPropertyPrimitive).runtimeType,
            );
            this.runtimeType = this.parseType(
                (this.eventProperty as EventPropertyPrimitive).runtimeType,
            );
        }
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
        if (node.semanticType !== undefined && SemanticType.isTimestamp(node)) {
            node.runtimeType = DataType.LONG;
            return true;
        } else {
            return false;
        }
    }

    public openEditDialog(eventProperty: EventProperty): void {
        const dialogRef = this.dialogService.open(EditEventPropertyComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Edit field ' + eventProperty.runtimeName,
            width: '50vw',
            data: {
                property: eventProperty,
                originalProperty: this.originalProperty,
            },
        });
        this.shepherdService.trigger('adapter-edit-field-clicked');

        dialogRef.afterClosed().subscribe(refresh => {
            this.timestampProperty = this.isTimestampProperty(
                this.eventProperty,
            );
            this.label = this.getLabel(this.eventProperty);
            this.checkAndDisplayProperties();
            this.refreshTreeEmitter.emit(true);
        });
    }

    asNestedProperty(property: EventProperty): EventPropertyNested {
        return property as EventPropertyNested;
    }
}
