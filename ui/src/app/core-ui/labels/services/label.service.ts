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

  getCategories(): Observable<any> {
    return this.$http.get(this.urlBase() + '/labeling/category');
  }

  getLabelsOfCategory(category: Category): Observable<any> {
    return this.$http.get(this.urlBase() + '/labeling/label/category/' + category._id);
  }

  addCategory(c: Category) {
    return this.$http.post(this.urlBase() + '/labeling/category', c);
  }

  updateCategory(c: Category) {
    return this.$http.put(this.urlBase() + '/labeling/category/' + c._id, c);
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
}
