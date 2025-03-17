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
    Input,
    Output,
    EventEmitter,
    OnInit,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { ColorMappingService } from '../../../services/color-mapping.service';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-color-mapping-options-config',
    templateUrl: './color-mapping-options-config.component.html',
})
export class ColorMappingOptionsConfigComponent implements OnInit, OnChanges {
    @Input() colorMapping: { value: string; label: string; color: string }[];

    @Input() selectedProperty: DataExplorerField;

    @Output()
    viewRefreshEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    colorMappingChange: EventEmitter<
        { value: string; label: string; color: string }[]
    > = new EventEmitter();

    protected isSelectedPropertyBoolean: boolean;
    protected showCustomColorMapping: boolean;
    private wasPreviousFieldBoolean: boolean;

    constructor(private colorMappingService: ColorMappingService) {}

    ngOnInit(): void {
        this.isSelectedPropertyBoolean = this.isBooleanPropertySelected();
        this.showCustomColorMapping ??= false;
        this.resetColorMappings();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (
            changes['selectedProperty'] &&
            !changes['selectedProperty'].firstChange
        ) {
            this.resetColorMappings();
            this.isSelectedPropertyBoolean = this.isBooleanPropertySelected();
        }
    }

    resetColorMappings(): void {
        const isNowBoolean = this.isBooleanPropertySelected();

        if (!this.showCustomColorMapping) {
            if (isNowBoolean) {
                this.colorMapping = [
                    { value: 'true', label: '', color: '#66BB66' },
                    { value: 'false', label: '', color: '#BB6666' },
                ];
            } else {
                this.colorMapping = [];
            }
        }
        if (isNowBoolean) {
            if (
                !(this.colorMapping ?? []).some(
                    mapping =>
                        mapping.value === 'true' || mapping.value === 'false',
                )
            ) {
                this.colorMapping = [
                    { value: 'true', label: '', color: '#66BB66' },
                    { value: 'false', label: '', color: '#BB6666' },
                ];
            }
        } else {
            if (this.wasPreviousFieldBoolean) {
                this.colorMapping = [];
            }
        }
        this.wasPreviousFieldBoolean = isNowBoolean;
        this.colorMappingChange.emit(this.colorMapping);
        this.viewRefreshEmitter.emit();
    }

    addMapping() {
        this.colorMappingService.addMapping(this.colorMapping);
        this.colorMappingChange.emit(this.colorMapping);
        this.viewRefreshEmitter.emit();
    }

    removeMapping(index: number) {
        this.colorMapping = this.colorMappingService.removeMapping(
            this.colorMapping,
            index,
        );
        this.colorMappingChange.emit(this.colorMapping);
        this.viewRefreshEmitter.emit();
    }

    updateColor(index: number, newColor: string) {
        this.colorMappingService.updateColor(
            this.colorMapping,
            index,
            newColor,
        );
        this.colorMappingChange.emit(this.colorMapping);
        this.viewRefreshEmitter.emit();
    }

    updateMapping() {
        this.colorMappingChange.emit(this.colorMapping);
        this.viewRefreshEmitter.emit();
    }

    isBooleanPropertySelected(): boolean {
        return this.selectedProperty.fieldCharacteristics.binary;
    }

    setCustomColorMapping(showCustomColorMapping: boolean) {
        this.showCustomColorMapping = showCustomColorMapping;

        if (!showCustomColorMapping) {
            this.resetColorMappings();
        }

        this.viewRefreshEmitter.emit();
    }
}
