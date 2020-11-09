import { Injectable } from '@angular/core';
import { Category, Label } from '../../../core-model/gen/streampipes-model';
import { PlatformServicesCommons } from '../../../platform-services/apis/commons.service';
import { HttpClient } from '@angular/common/http';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LabelService {
  private bufferedLabels = [];

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

  getAllLabels() {
    return this.$http.get(this.urlBase() + '/labeling/label/');
  }

  getLabel(labelId: string) {
    return this.$http.get(this.urlBase() + '/labeling/label/' + labelId);
  }

  getBufferedLabel(labelId: string) {
    // this.bufferedLabels
    const result = new ReplaySubject(1);

    if (this.bufferedLabels[labelId] !== undefined) {
     result.next(this.bufferedLabels[labelId]);
    } else {
      this.getLabel(labelId).subscribe(label => {
        this.bufferedLabels.push({labelId: label});
        result.next(label);
      });
    }

    return result;
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
