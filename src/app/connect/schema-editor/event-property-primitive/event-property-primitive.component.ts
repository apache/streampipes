import {Component, DoCheck, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EventProperty} from '../model/EventProperty';
// import {DragulaService} from 'ng2-dragula';
// import {DragDropService} from '../drag-drop.service';
import {Subscription} from 'rxjs/Subscription';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {DataTypesService} from '../data-type.service';
import {DomainPropertyProbabilityList} from '../model/DomainPropertyProbabilityList';
import {ShepherdService} from '../../../services/tour/shepherd.service';
import {RestService} from '../../rest.service';
import {UnitDescription} from '../../model/UnitDescription';
import {UnitProviderService} from '../unit-provider.service';
import {Observable} from 'rxjs/Observable';
import {map, startWith} from 'rxjs/operators';

// import {DataTypesService} from '../data-type.service';

@Component({
  selector: 'app-event-property-primitive',
  templateUrl: './event-property-primitive.component.html',
  styleUrls: ['./event-property-primitive.component.css']
})
export class EventPropertyPrimitiveComponent implements OnInit, DoCheck {

  @Input() property: EventPropertyPrimitive;
  @Input() index: number;

  @Input() domainPropertyGuess: DomainPropertyProbabilityList;

  @Input()
  isEditable: Boolean;


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
  private filteredUnits: Observable<UnitDescription[]>;
  private hadMesarumentUnit = false;
  private oldMeasurementUnitDipsplay;

  // Todo:
  // Set in property to create tranformationrule
  // Make sure, that just create tranfomationrule if timestamp!
  private timeConverter;
  private customTimeMultiplier;
  private selectedTimeMultiplierNumber = 1000;
  private useCustomMultiplier;
    // Don't add
  private selectedTimeMultiplier = "second";


    constructor(private formBuilder: FormBuilder,
              private dataTypeService: DataTypesService,
              private ShepherdService: ShepherdService,
              private restService: RestService,
              private unitProviderService: UnitProviderService
  ) {
      this.dataTypeService = dataTypeService;
      // constructor(private dragulaService: DragulaService, private formBuilder: FormBuilder) {
      // constructor(private dragulaService: DragulaService, private formBuilder: FormBuilder, private dataTypesService: DataTypesService) {
    // this.propertyPrimitivForm = formBuilder.group({
    //   dataType: ['', Validators.required]
    // });

    this.runtimeDataTypes = this.dataTypeService.getDataTypes();

    this.allUnits = this.unitProviderService.getUnits();
    this.filteredUnits = this.stateCtrl.valueChanges
        .pipe(
            startWith(''),
            map(unit => unit ? this._filteredUnits(unit) : this.allUnits.slice())
        );


  }

  protected open = false;
  subscription: Subscription;


  ngOnInit() {
  //   this.dragulaService.drag.subscribe((value: any) => this.drag());
  //   this.property.propertyNumber = this.index;
      if (this.property.measurementUnit !== undefined) {
          this.property.oldMeasurementUnit = this.property.measurementUnit;
          // TODO: use if backend deserialize URI correct
          this.property.measurementUnitTmp = this.property.measurementUnit;
          this.hadMesarumentUnit = true;
          const unit = this.allUnits.find(unitTmp => unitTmp.resource === this.property.measurementUnit);
          this.oldMeasurementUnitDipsplay = unit.label;
          this.stateCtrl.setValue(unit.label);
      }
  }

  ngDoCheck() {
    this.property.propertyNumber = this.index;
  }

  // von Dragula-Service aufgerufen nach Drag dieses Elements
  private drag() {
    // const dragDropService: DragDropService = DragDropService.getInstance();
    // dragDropService.announceDrag(this.property);
    // dragDropService.nestConfirmed$.subscribe(result => {
    //   this.property.parent = result;
    // });
  }

  private OnClickDeleteProperty(): void {
    this.delete.emit(this.property);
  }

  private OnClickOpen(): void {
    this.open = !this.open;
      this.ShepherdService.trigger("open-event-property-primitve");
  }

  // TODO this works not completely correct
  private getLabel(): string {
    if (typeof this.property.label !== 'undefined') {
      return this.property.label;
    } else if (typeof this.property.runTimeName !== 'undefined') {
        return this.property.runTimeName;
    } else {
      return 'Property';
    }
  }

  private transformUnit() {
    if (this.transformUnitEnable) {
      this.transformUnitEnable = false;
        // TODO: use if backend deserialize URI correct
        // this.property.measurementUnit = this.property.oldMeasurementUnit;
        this.property.measurementUnitTmp = this.property.oldMeasurementUnit;
    } else {
      const unit = this.allUnits.find(unitTmp => unitTmp.label === this.stateCtrl.value);
      if (!unit) {
        return;
      }

      this.restService.getFittingUnits(unit).subscribe( result => {
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
  }

  staticValueAddedByUser() {
      if (this.property.id.startsWith('http://eventProperty.de/staticValue/')){
          return true;
      } else {
          return false;
      }

  }

  isTimestampProperty() {
      return this.property.domainProperty === "http://schema.org/DateTime"
  }

}
