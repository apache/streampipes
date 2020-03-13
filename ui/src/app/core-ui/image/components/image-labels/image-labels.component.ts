import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ColorService } from "../../services/color.service";

@Component({
  selector: 'sp-image-labels',
  templateUrl: './image-labels.component.html',
  styleUrls: ['./image-labels.component.css']
})
export class ImageLabelsComponent implements OnInit {

  @Input()
  set labels(labels) {
    this._labels = labels;
    this.update();
  }
  @Input() enableShortCuts: boolean;
  @Output() labelChange: EventEmitter<{category, label}> = new EventEmitter<{category, label}>();

  public _labels;
  public _selectedLabel: {category, label};
  public categories;
  public selectedCategory;

  constructor(public colorService: ColorService) { }

  ngOnInit(): void {

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

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(event: KeyboardEvent) {
    if (this.enableShortCuts) {
      if (event.code.toLowerCase().includes('digit')) {
        // Number
        const value = Number(event.key);
        if (value !== 0 && value <= this._labels[this.selectedCategory].length) {
          this.selectLabel({category: this.selectedCategory, label: this._labels[this.selectedCategory][value - 1]});
        }
      }
    }
  }

}
