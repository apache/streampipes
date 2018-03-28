import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventProperty} from '../model/EventProperty';
import {WriteJsonService} from '../write-json.service';
import {DragDropService} from '../drag-drop.service';
// import {dataTypes} from '../data-model';

@Component({
  selector: 'app-event-property',
  templateUrl: './event-property.component.html',
  styleUrls: ['./event-property.component.css']
})

export class EventPropertyComponent implements OnInit {

  private propertyForm: FormGroup;
  // protected dataTypes = dataTypes;

  @Input() property: EventProperty;

  @Output() save: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();

  constructor(private formBuilder: FormBuilder) {
  }


  private createForm() {
    this.propertyForm = this.formBuilder.group({
      label: [this.property.getLabel(), Validators.required],
      runtimeName: [this.property.getRuntimeName(), Validators.required],
      description: [this.property.getDescription(), Validators.required],
      domainProperty: ['', Validators.required],
      dataType: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.createForm();
    console.log(this.property);
  }

  // aufgerufen, wenn FormGroup valide ist
  submit(): void {
    const writeJsonService: WriteJsonService = WriteJsonService.getInstance();
    const dragDropService: DragDropService = DragDropService.getInstance();

    this.property.label = this.propertyForm.value.label;
    this.property.description = this.propertyForm.value.description;
    this.property.runTimeName = this.propertyForm.value.runtimeName;
    this.property.domainProperty = encodeURIComponent(this.propertyForm.value.domainProperty);

    const path: string = dragDropService.buildPath(this.property);
    writeJsonService.add(path, this.propertyForm.value.runtimeName);
    this.save.emit(this.property);
  }
}
