import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { AnyStaticProperty } from '../../model/AnyStaticProperty';

@Component({
  selector: 'app-static-any-input',
  templateUrl: './static-any-input.component.html',
  styleUrls: ['./static-any-input.component.css'],
})
export class StaticAnyInput implements OnInit {
  @Input()
  staticProperty: AnyStaticProperty;

  @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();


  ngOnInit() {
    for (let option of this.staticProperty.options) {
      option.selected = false;
    }
    this.inputEmitter.emit(true);
  }
}
