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

import {Component, Input, OnInit} from '@angular/core';
import {NodeInfoDescription} from "../../../core-model/gen/streampipes-model";
import {FormGroup} from "@angular/forms";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {MatChipInputEvent} from "@angular/material/chips";
import {COMMA, ENTER} from "@angular/cdk/keycodes";

export interface Fruit {
  name: string;
}

@Component({
  selector: 'node-configuration-details',
  templateUrl: './node-configuration-details.component.html',
  styleUrls: ['./node-configuration-details.component.scss']
})
export class NodeConfigurationDetailsComponent implements OnInit {

  submitNodeForm: FormGroup = new FormGroup({});
  saving: boolean = false;
  saved: boolean = false;
  advancedSettings: boolean;

  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tmpTags: string[];

  @Input()
  node: NodeInfoDescription;

  constructor(private dialogRef: DialogRef<NodeConfigurationDetailsComponent>) {
  }

  ngOnInit(): void {
    this.advancedSettings = false;
    this.tmpTags = this.node.staticNodeMetadata.locationTags.map(x => x);
  }

  updateNodeInfo() {

  }

  hide() {
    this.dialogRef.close();
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      this.tmpTags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(tag: string): void {
    const index = this.tmpTags.indexOf(tag);

    if (index >= 0) {
      this.tmpTags.splice(index, 1);
    }
  }

}
