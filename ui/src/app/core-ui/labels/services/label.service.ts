/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Injectable } from '@angular/core';
import {
    Category,
    Label,
    PlatformServicesCommons,
} from '@streampipes/platform-services';
import { HttpClient } from '@angular/common/http';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class LabelService {
    private bufferedLabels = [];

    urlBase() {
        return this.platformServicesCommons.apiBasePath;
    }

    constructor(
        private platformServicesCommons: PlatformServicesCommons,
        private $http: HttpClient,
    ) {}

    getCategories(): Observable<any> {
        return this.$http.get(this.urlBase() + '/labeling/category');
    }

    getLabelsOfCategory(category: Category): Observable<any> {
        return this.$http.get(
            this.urlBase() + '/labeling/label/category/' + category._id,
        );
    }

    addCategory(c: Category) {
        return this.$http.post(this.urlBase() + '/labeling/category', c);
    }

    updateCategory(c: Category) {
        return this.$http.put(
            this.urlBase() + '/labeling/category/' + c._id,
            c,
        );
    }

    deleteCategory(c: Category) {
        return this.$http.delete(
            this.urlBase() + '/labeling/category/' + c._id,
        );
    }

    getAllLabels() {
        return this.$http.get(this.urlBase() + '/labeling/label/');
    }

    getLabel(labelId: string) {
        return this.$http.get(this.urlBase() + '/labeling/label/' + labelId);
    }

    getBufferedLabel(labelId: string) {
        const result = new ReplaySubject(1);

        if (this.bufferedLabels[labelId] !== undefined) {
            result.next(this.bufferedLabels[labelId]);
        } else {
            this.getLabel(labelId).subscribe(label => {
                this.bufferedLabels.push({ labelId: label });
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
