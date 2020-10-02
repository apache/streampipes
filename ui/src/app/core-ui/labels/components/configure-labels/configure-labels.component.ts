import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ColorService } from '../../../image/services/color.service';
import { LabelService } from '../../services/label.service';
import { Category, Label } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-configure-labels',
  templateUrl: './configure-labels.component.html',
  styleUrls: ['./configure-labels.component.css']
})
export class ConfigureLabelsComponent implements OnInit {

  public categories: Category[];
  public selectedCategory: Category;

  constructor(public colorService: ColorService, public labelService: LabelService) { }

  ngOnInit(): void {
    this.categories = this.labelService.getCategories();
    this.update();
  }

  update() {
    this.selectedCategory = this.categories[0];
  }

  addCategory() {
  }

  addLabel() {
    const label = new Label();
    label.name = 'test';

  }
}
