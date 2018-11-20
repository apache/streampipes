import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AnyStaticProperty} from '../../model/AnyStaticProperty';
import {OneOfStaticProperty} from '../../model/OneOfStaticProperty';

@Component({
  selector: 'app-static-one-of-input',
  templateUrl: './static-one-of-input.component.html',
  styleUrls: ['./static-one-of-input.component.css']
})
export class StaticOneOfInputComponent implements OnInit {

  @Input()
  staticProperty: OneOfStaticProperty;

  @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

  constructor() { }

  ngOnInit() {
      for (let option of this.staticProperty.options) {
          option.selected = false;
      }
  }

  select(id) {
      for (let option of this.staticProperty.options) {
          option.selected = false;
      }
      this.staticProperty.options.find(option => option.id === id).selected = true;
      this.inputEmitter.emit(true)
  }

}
