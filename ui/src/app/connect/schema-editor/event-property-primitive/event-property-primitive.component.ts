/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Component, DoCheck, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EventProperty } from '../model/EventProperty';
import { Subscription, Observable } from 'rxjs';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DataTypesService } from '../data-type.service';
import { DomainPropertyProbabilityList } from '../model/DomainPropertyProbabilityList';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { RestService } from '../../rest.service';
import { UnitDescription } from '../../model/UnitDescription';
import { UnitProviderService } from '../unit-provider.service';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-event-property-primitive',
  templateUrl: './event-property-primitive.component.html',
  styleUrls: ['./event-property-primitive.component.css']
})
export class EventPropertyPrimitiveComponent implements OnInit, DoCheck {

  @Input() property: EventPropertyPrimitive;
  @Input() index: number;

  @Input() domainPropertyGuess: DomainPropertyProbabilityList;

  @Input() isEditable: boolean;


  private propertyPrimitivForm: FormGroup;
  private runtimeDataTypes;
  @Output() delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addPrimitive: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addNested: EventEmitter<any> = new EventEmitter<any>();

  private transformUnitEnable = false;
  private possibleUnitTransformations: UnitDescription[] = [];
  private selectUnit: UnitDescription;
  private allUnits: UnitDescription[];
  private stateCtrl = new FormControl();

  private newUnitStateCtrl = new FormControl();
  private filteredUnits: Observable<UnitDescription[]>;
  private hadMesarumentUnit = false;
  private oldMeasurementUnitDipsplay;

  private selectedTimeMultiplier;



  constructor(private formBuilder: FormBuilder,
    private dataTypeService: DataTypesService,
    private ShepherdService: ShepherdService,
    private restService: RestService,
    private unitProviderService: UnitProviderService) {
    this.dataTypeService = dataTypeService;

    this.runtimeDataTypes = this.dataTypeService.getDataTypes();

    this.allUnits = this.unitProviderService.getUnits();
    this.filteredUnits = this.stateCtrl.valueChanges
      .pipe(
        startWith(''),
        map(unit => unit ? this._filteredUnits(unit) : this.allUnits.slice())
      );

    // Set preselected value
    this.selectedTimeMultiplier = "second";
  }

  protected open = false;
  subscription: Subscription;


  ngOnInit() {
    //   this.property.propertyNumber = this.index;
    if (this.property.measurementUnitTmp !== undefined) {
      this.property.oldMeasurementUnit = this.property.oldMeasurementUnit;
      // TODO: use if backend deserialize URI correct
      this.property.measurementUnitTmp = this.property.measurementUnitTmp;
      this.hadMesarumentUnit = this.property.hadMeasarumentUnit;
      this.transformUnitEnable = this.property.hadMeasarumentUnit;
      const unit = this.allUnits.find(unitTmp => unitTmp.resource === this.property.oldMeasurementUnit);
      this.oldMeasurementUnitDipsplay = unit.label;
      this.stateCtrl.setValue(unit.label);

      this.restService.getFittingUnits(unit).subscribe(result => {
        this.possibleUnitTransformations = result;
        // this.selectUnit = this.possibleUnitTransformations[0];
        this.selectUnit = this.allUnits.find(unitTmp => unitTmp.resource === this.property.measurementUnitTmp);
        this.transformUnitEnable = true
        this.changeTargetUnit(this.selectUnit);
      });
      // const newUnit = this.allUnits.find(unitTmp => unitTmp.resource === this.property.measurementUnitTmp);
      // this.newUnitStateCtrl.setValue(newUnit);
      // this.selectUnit = newUnit;
    }
    this.property.timestampTransformationMultiplier = 1000;

  }

    compareFn(c1: any, c2:any): boolean {
        return c1 && c2 ? c1.resource === c2.resource : c1 === c2;
    }

  ngDoCheck() {
    this.property.propertyNumber = this.index;
  }

  private transformUnit() {
    if (this.transformUnitEnable) {
      this.transformUnitEnable = false;
      // TODO: use if backend deserialize URI correct
      // this.property.measurementUnit = this.property.oldMeasurementUnit;
      this.property.measurementUnitTmp = this.property.oldMeasurementUnit;
      this.property.hadMeasarumentUnit = false;
    } else {
      const unit = this.allUnits.find(unitTmp => unitTmp.label === this.stateCtrl.value);
      this.property.hadMeasarumentUnit = true;
      if (!unit) {
        return;
      }

      this.restService.getFittingUnits(unit).subscribe(result => {
        this.possibleUnitTransformations = result;
        this.selectUnit = this.possibleUnitTransformations[0];
        this.transformUnitEnable = true
        this.changeTargetUnit(this.selectUnit);
      });
    }
  }

  private _filteredUnits(value: string): UnitDescription[] {
    const filterValue = value.toLowerCase();
    const units: UnitDescription[] = this.allUnits.filter(unit => unit.label.toLowerCase().indexOf(filterValue) === 0);
    const unit: UnitDescription = this.allUnits.filter(unit => unit.label.toLocaleLowerCase() === filterValue)[0];
    if (unit !== undefined) {
      this.property.oldMeasurementUnit = unit.resource;
      this.property.measurementUnitTmp = unit.resource;
      // TODO: use if backend deserialize URI correct
      //   this.property.measurementUnit = units.resource;
    } else {
      this.property.oldMeasurementUnit = undefined;
      this.property.measurementUnitTmp = undefined;
      // TODO: use if backend deserialize URI correct
      //   this.property.measurementUnit = undefined;
    }
    return units;
  }

  changeTargetUnit(unit: UnitDescription) {
    // TODO: use if backend deserialize URI correct
    // this.property.measurementUnit = unit.resource;
    this.property.measurementUnitTmp = unit.resource;
      this.newUnitStateCtrl.setValue(unit);
  }

  staticValueAddedByUser() {
    if (this.property.id.startsWith('http://eventProperty.de/staticValue/')) {
      return true;
    } else {
      return false;
    }

  }

}
