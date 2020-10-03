import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Label } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-label-list-item',
  templateUrl: './label-list-item.component.html',
  styleUrls: ['./label-list-item.component.css']
})
export class LabelListItemComponent implements OnInit {

  @Input()
  label: Label;

  @Output() removeLabel = new EventEmitter<Label>();

  constructor() { }

  ngOnInit(): void {
  }

  public clickRemoveLabel() {
    this.removeLabel.emit(this.label);
  }

}
