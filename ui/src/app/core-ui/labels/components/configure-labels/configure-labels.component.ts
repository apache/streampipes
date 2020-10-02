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

  public editCategory: boolean;

  constructor(public colorService: ColorService, public labelService: LabelService) { }

  ngOnInit(): void {
    this.editCategory = false;
    this.categories = this.labelService.getCategories();
  }

  startEditCategory(value) {
    if ('internal_placeholder' !== value.value) {
      this.editCategory = true;
    }
  }

  endEditCategory() {
    this.selectedCategory = null;
    this.editCategory = false;
  }

  addCategory() {
    const c1 = new Category();
    c1.name = '';
    c1.labels = [];

    this.categories.push(c1);
    this.selectedCategory = c1;
    this.editCategory = true;
  }

  addLabel() {
    const label = new Label();
    label.name = '';
    label.color = '#' + (Math.random() * 0xFFFFFF << 0).toString(16).padStart(6, '0');

    this.selectedCategory.labels.push(label);
  }

  removeLabel(label) {
    this.selectedCategory.labels = this.selectedCategory.labels.filter(obj => obj !== label);
  }
}
