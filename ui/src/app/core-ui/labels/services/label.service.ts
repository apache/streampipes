import { Injectable } from '@angular/core';
import { Category, Label } from '../../../core-model/gen/streampipes-model';

@Injectable({
  providedIn: 'root'
})
export class LabelService {
  private categories = [];

  constructor() { }

  getLabels() {
    return {
      'boxes': ['blue', 'red'],
      'sign': ['trafficsign'],
      'person': ['person', 'Child'],
      'vehicle': ['bicycle', 'car', 'motorcycle', 'airplane', 'bus', 'train', 'truck', 'boat'],
      'outdoor': ['traffic light', 'fire hydrant', 'stop sign', 'parking meter', 'bench'],
      'animal': ['bird', 'cat', 'dog'],
      'accessory': ['backpack', 'umbrella', 'handbag', 'suitcase'],
      'sports': ['frisbee', 'sports ball', 'skis', 'frisbee', 'baseball bat'],
      'kitchen': ['bottle', 'cup', 'fork', 'knife', 'spoon'],
      'furniture': ['chair', 'couch', 'bed', 'table'],
      'electronic': ['tv', 'laptop', 'mouse', 'keyboard']
    };
  }

  getCategories(): Category[] {

    const c1 = new Category();
    c1.name = 'Straßenschild';

    c1.labels = [];
    c1.labels.push(this.getLabel('Stop', '#0000ff'));
    c1.labels.push(this.getLabel('50', '#ff0000'));

    const c2 = new Category();
    c2.name = 'furniture';

    c2.labels = [];
    c2.labels.push(this.getLabel('chair', '#7dff24'));
    c2.labels.push(this.getLabel('couch', '#ff6ef7'));

    this.categories.push(c1);
    this.categories.push(c2);

    return this.categories;
  }


  addCategory(c: Category) {
   this.categories.push(c);
  }

  updateCategory(c: Category) {
    this.categories.push(c);
  }


  private getLabel(name, color): Label {
    const label1 = new Label();
    label1.name = name;
    label1.color = color;
    return label1;
  }
}
