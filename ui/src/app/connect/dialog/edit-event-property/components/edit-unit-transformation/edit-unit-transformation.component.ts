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

import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UnitDescription } from '../../../../model/UnitDescription';
import { RestService } from '../../../../services/rest.service';
import { UnitProviderService } from '../../../../services/unit-provider.service';

@Component({
    selector: 'sp-edit-unit-transformation',
    templateUrl: './edit-unit-transformation.component.html',
    styleUrls: ['./edit-unit-transformation.component.scss'],
})
export class EditUnitTransformationComponent implements OnInit {
    @Input() cachedProperty: any;

    @Input() isTimestampProperty: boolean;
    @Input() isNestedProperty: boolean;
    @Input() isListProperty: boolean;
    @Input() isPrimitiveProperty: boolean;
    @Input() isNumericProperty: boolean;

    @Input() showUnitTransformation: boolean;

    private transformUnitEnable = false;
    private possibleUnitTransformations: UnitDescription[] = [];
    private selectUnit: UnitDescription;
    private allUnits: UnitDescription[];
    private stateCtrl = new UntypedFormControl();

    private newUnitStateCtrl = new UntypedFormControl();
    private filteredUnits: Observable<UnitDescription[]>;
    public hadMeasurementUnit = false;
    private oldMeasurementUnitDipsplay;

    constructor(
        private formBuilder: UntypedFormBuilder,
        private restService: RestService,
        private unitProviderService: UnitProviderService,
    ) {
        this.allUnits = this.unitProviderService.getUnits();
        this.filteredUnits = this.stateCtrl.valueChanges.pipe(
            startWith(''),
            map(unit =>
                unit ? this._filteredUnits(unit) : this.allUnits.slice(),
            ),
        );
    }

    protected open = false;

    ngOnInit() {
        //   this.cachedProperty.propertyNumber = this.index;
        if ((this.cachedProperty as any).measurementUnitTmp !== undefined) {
            (this.cachedProperty as any).oldMeasurementUnit = (
                this.cachedProperty as any
            ).oldMeasurementUnit;
            // TODO: use if backend deserialize URI correct
            (this.cachedProperty as any).measurementUnitTmp = (
                this.cachedProperty as any
            ).measurementUnitTmp;
            this.hadMeasurementUnit = (
                this.cachedProperty as any
            ).hadMeasarumentUnit;
            this.transformUnitEnable = (
                this.cachedProperty as any
            ).hadMeasarumentUnit;
            const unit = this.allUnits.find(
                unitTmp =>
                    unitTmp.resource ===
                    (this.cachedProperty as any).oldMeasurementUnit,
            );
            this.oldMeasurementUnitDipsplay = unit.label;
            this.stateCtrl.setValue(unit.label);

            this.restService.getFittingUnits(unit).subscribe(result => {
                this.possibleUnitTransformations = result;
                // this.selectUnit = this.possibleUnitTransformations[0];
                this.selectUnit = this.allUnits.find(
                    unitTmp =>
                        unitTmp.resource ===
                        (this.cachedProperty as any).measurementUnitTmp,
                );
                // this.transformUnitEnable = true;
                this.changeTargetUnit(this.selectUnit);
            });
        }
    }

    compareFn(c1: any, c2: any): boolean {
        return c1 && c2 ? c1.resource === c2.resource : c1 === c2;
    }

    private transformUnit() {
        if (this.transformUnitEnable) {
            this.transformUnitEnable = false;
            // TODO: use if backend deserialize URI correct
            // this.cachedProperty.measurementUnit = this.cachedProperty.oldMeasurementUnit;
            (this.cachedProperty as any).measurementUnitTmp = (
                this.cachedProperty as any
            ).oldMeasurementUnit;
            (this.cachedProperty as any).hadMeasarumentUnit = false;
        } else {
            const unit = this.allUnits.find(
                unitTmp => unitTmp.label === this.stateCtrl.value,
            );
            (this.cachedProperty as any).hadMeasarumentUnit = true;
            if (!unit) {
                return;
            }

            this.restService.getFittingUnits(unit).subscribe(result => {
                this.possibleUnitTransformations = result;
                this.selectUnit = this.possibleUnitTransformations[0];
                this.transformUnitEnable = true;
                this.changeTargetUnit(this.selectUnit);
            });
        }
    }

    private _filteredUnits(value: string): UnitDescription[] {
        const filterValue = value.toLowerCase();
        const units: UnitDescription[] = this.allUnits.filter(
            unit => unit.label.toLowerCase().indexOf(filterValue) === 0,
        );
        const unit: UnitDescription = this.allUnits.filter(
            unit => unit.label.toLocaleLowerCase() === filterValue,
        )[0];
        if (unit !== undefined) {
            (this.cachedProperty as any).oldMeasurementUnit = unit.resource;
            (this.cachedProperty as any).measurementUnitTmp = unit.resource;
            // TODO: use if backend deserialize URI correct
            //   this.cachedProperty.measurementUnit = units.resource;
        } else {
            (this.cachedProperty as any).oldMeasurementUnit = undefined;
            (this.cachedProperty as any).measurementUnitTmp = undefined;
            // TODO: use if backend deserialize URI correct
            //   this.cachedProperty.measurementUnit = undefined;
        }
        return units;
    }

    changeTargetUnit(unit: UnitDescription) {
        // TODO: use if backend deserialize URI correct
        // this.cachedProperty.measurementUnit = unit.resource;
        (this.cachedProperty as any).measurementUnitTmp = unit.resource;
        this.newUnitStateCtrl.setValue(unit);
    }

    // setShowUnitTransformation() {
    //   this.hideUnitTransformation = this.isTimestampProperty ||
    //     !this.dataTypesService.isNumeric(this.cachedProperty.runtimeType);
    //
    //   if (this.dataTypesService.isNumeric(this.cachedProperty.runtimeType)) {
    //     this.isNumericDataType.emit(true);
    //   } else {
    //     this.isNumericDataType.emit(false);
    //   }
    // }
}
