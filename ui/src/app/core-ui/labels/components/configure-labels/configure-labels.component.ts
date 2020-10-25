import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ColorService } from '../../../image/services/color.service';
import { LabelService } from '../../services/label.service';
import { Category, Label } from '../../../../core-model/gen/streampipes-model';
import { fromEvent, interval, timer } from 'rxjs';
import { debounce, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { ajax } from 'rxjs/ajax';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'sp-configure-labels',
  templateUrl: './configure-labels.component.html',
  styleUrls: ['./configure-labels.component.css']
})
export class ConfigureLabelsComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, public colorService: ColorService, public labelService: LabelService) { }

  public categories: Category[];
  public selectedCategory: Category;
  public categoryLabels: Label[];

  public editCategory: boolean;

  ngOnInit(): void {
    this.editCategory = false;
    this.labelService.getCategories().subscribe(res => {
      this.categories = res;
    });
  }


  startEditCategory(value) {
    if ('internal_placeholder' !== value.value) {
      this.editCategory = true;
    }

    this.labelService.getLabelsOfCategory(this.selectedCategory).subscribe((res: Label[]) => {
      this.categoryLabels = res;
    });
  }

  endEditCategory() {
    this.selectedCategory = null;
    this.editCategory = false;
  }

  addCategory() {
    const c1 = new Category();
    c1.name = '';

    this.labelService.addCategory(c1).subscribe((res: Category) => {
      this.selectedCategory = res;
      this.editCategory = true;
      this.categories.push(res);
    });

    this.categoryLabels = [];
  }

  updateCategory(newCategoryName) {
    this.selectedCategory.name = newCategoryName;

    this.labelService.updateCategory(this.selectedCategory)
      .subscribe((res: Category) => {
      this.categories = this.categories.filter(obj => obj !== this.selectedCategory);
      this.categories.push(res);
      this.selectedCategory = res;
    });
  }

  deleteCategory() {
    this.labelService.deleteCategory(this.selectedCategory).subscribe();
    this.categories = this.categories.filter(obj => obj !== this.selectedCategory);
    this.endEditCategory();
  }

  addLabel() {
    const label = new Label();
    label.name = '';
    // tslint:disable-next-line:no-bitwise
    label.color = '#' + (Math.random() * 0xFFFFFF << 0).toString(16).padStart(6, '0');
    label.categoryId = this.selectedCategory._id;

    this.labelService.addLabel(label).subscribe((res: Label) => {
      this.categoryLabels.push(res);
    });
  }

  removeLabel(label) {
    this.labelService.deleteLabel(label).subscribe();
    this.categoryLabels = this.categoryLabels.filter(obj => obj !== label);
  }
}
