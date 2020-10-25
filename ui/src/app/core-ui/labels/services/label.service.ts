import { Injectable } from '@angular/core';
import { Category, Label } from '../../../core-model/gen/streampipes-model';
import { PlatformServicesCommons } from '../../../platform-services/apis/commons.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LabelService {
  private categories = [];

  urlBase() {
    return this.platformServicesCommons.authUserBasePath();
  }

  constructor(
    private platformServicesCommons: PlatformServicesCommons,
    private $http: HttpClient) { }

  // getLabels() {
  //   return {
  //     'boxes': ['blue', 'red'],
  //     'sign': ['trafficsign'],
  //     'person': ['person', 'Child'],
  //     'vehicle': ['bicycle', 'car', 'motorcycle', 'airplane', 'bus', 'train', 'truck', 'boat'],
  //     'outdoor': ['traffic light', 'fire hydrant', 'stop sign', 'parking meter', 'bench'],
  //     'animal': ['bird', 'cat', 'dog'],
  //     'accessory': ['backpack', 'umbrella', 'handbag', 'suitcase'],
  //     'sports': ['frisbee', 'sports ball', 'skis', 'frisbee', 'baseball bat'],
  //     'kitchen': ['bottle', 'cup', 'fork', 'knife', 'spoon'],
  //     'furniture': ['chair', 'couch', 'bed', 'table'],
  //     'electronic': ['tv', 'laptop', 'mouse', 'keyboard']
  //   };
  // }

  getCategories(): Observable<any> {
    return this.$http.get(this.urlBase() + '/labeling/category');
    //
    // const c1 = new Category();
    // c1.name = 'Straßenschild';
    //
    // c1.labels = [];
    // c1.labels.push(this.getLabel('Stop', '#0000ff'));
    // c1.labels.push(this.getLabel('50', '#ff0000'));
    //
    // const c2 = new Category();
    // c2.name = 'furniture';
    //
    // c2.labels = [];
    // c2.labels.push(this.getLabel('chair', '#7dff24'));
    // c2.labels.push(this.getLabel('couch', '#ff6ef7'));
    //
    // this.categories.push(c1);
    // this.categories.push(c2);
    //
    // return this.categories;
  }

  getLabelsOfCategory(category: Category): Observable<any> {
    return this.$http.get(this.urlBase() + '/labeling/label/category/' + category._id);
  }


  addCategory(c: Category) {
    return this.$http.post(this.urlBase() + '/labeling/category', c);
    // this.categories.push(c);
  }

  updateCategory(c: Category) {
    return this.$http.put(this.urlBase() + '/labeling/category/' + c._id, c);
    // this.categories.push(c);
  }

  deleteCategory(c: Category) {
    return this.$http.delete(this.urlBase() + '/labeling/category/' + c._id);
  }

  addLabel(l: Label) {
    return this.$http.post(this.urlBase() + '/labeling/label', l);
  }

  updateLabel(l: Label) {
    return this.$http.put(this.urlBase() + '/labeling/label/' + l._id, l);
  }

  deleteLabel(l: Label) {
    return this.$http.delete(this.urlBase() + '/labeling/label/' + l._id);
  }


  private getLabel(name, color): Label {
    const label1 = new Label();
    label1.name = name;
    label1.color = color;
    return label1;
  }
}
