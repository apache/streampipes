import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Category, Label } from '../../../../core-model/gen/streampipes-model';
import { ColorService } from '../../../image/services/color.service';
import { LabelService } from '../../services/label.service';

@Component({
  selector: 'sp-label-list-item',
  templateUrl: './label-list-item.component.html',
  styleUrls: ['./label-list-item.component.css']
})
export class LabelListItemComponent implements OnInit {

  @Input()
  label: Label;

  @Output() removeLabel = new EventEmitter<Label>();

  constructor(public labelService: LabelService) { }

  ngOnInit(): void {
  }

  public clickRemoveLabel() {
    this.removeLabel.emit(this.label);
  }

  public updateLabelName(newLabelName) {
    this.label.name = newLabelName;
    this.updateLabel();
  }

  public updateLabelColor(newLabelColor) {
    this.label.color = newLabelColor;
    this.updateLabel();
  }

  private updateLabel() {
    this.labelService.updateLabel(this.label).subscribe((res: Label) => {
      this.label = res;
    });
  }
}
