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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { StaticPropertyUtilService } from '../../static-property-util.service';
import { EventProperty, OneOfStaticProperty, StaticProperty } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-add-to-collection',
  templateUrl: './add-to-collection.component.html',
  styleUrls: ['./add-to-collection.component.css']
})
export class AddToCollectionComponent implements OnInit {

  @Input()
  public staticPropertyTemplate: StaticProperty;

  @Output()
  addPropertyEmitter: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();

  public showFileSelecion = false;

  constructor(private staticPropertyUtil: StaticPropertyUtilService) {
  }

  ngOnInit(): void {
  }

  add() {
    const clone = this.staticPropertyUtil.clone(this.staticPropertyTemplate);
    this.addPropertyEmitter.emit(clone);
  }

  selectFileSelection() {
    this.showFileSelecion = true;
  }

  closeFileSelection() {
    this.showFileSelecion = false;
  }

  handleFileInput(files: any) {
    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      const res = this.parseCsv(fileReader.result);
      res.pop();

      res.forEach(row => {
        const clone = this.staticPropertyUtil.clone(this.staticPropertyTemplate);
        clone.staticProperties.forEach(p => {
          if (p instanceof OneOfStaticProperty) {
            const option = p.options.find(o => o.name === row[p.label]);
            option.selected = true;
          } else {
            p.value = row[p.label];
          }
        });
        this.addPropertyEmitter.emit(clone);
      });

    };
    fileReader.readAsText(files[0]);
  }

  private parseCsv(str) {
    const delimiter = ';';
    const headers = str.slice(0, str.indexOf('\n')).split(delimiter);

    const rows = str.slice(str.indexOf('\n') + 1).split('\n');

    const arr = rows.map(row => {
      const values = row.split(delimiter);
      const el = headers.reduce((object, header, index) => {
        object[header] = values[index];
        return object;
      }, {});
      return el;
    });

    return arr;
  }
}
