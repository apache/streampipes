import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { GridsterItem } from 'angular-gridster2';

@Component({
  selector: 'sp-select-keys',
  templateUrl: './select-keys.component.html',
  styleUrls: ['./select-keys.component.css']
})
export class SelectKeysComponent implements OnInit {

  @Output()
  changeSelectedKeys: EventEmitter<string[]> = new EventEmitter();

  @Input()
  availableKeys: string[];

  @Input()
  selectedKeys: string[];


  constructor() { }

  ngOnInit(): void {
  }

  triggerSelectedKeys(keys: string[]) {
    this.changeSelectedKeys.emit(keys);
  }

}
