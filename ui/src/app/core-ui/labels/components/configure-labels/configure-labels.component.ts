import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ColorService } from '../../../image/services/color.service';
import { LabelService } from '../../services/label.service';

@Component({
  selector: 'sp-configure-labels',
  templateUrl: './configure-labels.component.html',
  styleUrls: ['./configure-labels.component.css']
})
export class ConfigureLabelsComponent implements OnInit {

  @Input() enableShortCuts: boolean;
  @Output() labelChange: EventEmitter<{category, label}> = new EventEmitter<{category, label}>();

  public _labels;
  public _selectedLabel: {category, label};
  public categories;
  public selectedCategory;

  constructor(public colorService: ColorService, public labelService: LabelService) { }

  ngOnInit(): void {
    this._labels = this.labelService.getLabels();
    this.update();
  }

  update() {
    this.categories = Object.keys(this._labels);
    this.selectedCategory = this.categories[0];
    this._selectedLabel = {category: this.selectedCategory, label: this._labels[this.selectedCategory][0]};
    this.labelChange.emit(this._selectedLabel);
  }

  selectLabel(e: {category, label}) {
    this._selectedLabel = e;
    this.labelChange.emit(this._selectedLabel);
  }

}
