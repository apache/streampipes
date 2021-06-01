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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NodeInfoDescription} from "../../../../core-model/gen/streampipes-model";

export interface NodeTags {
  name: string;
  selected: boolean;
}
@Component({
  selector: 'node-tag-selector',
  templateUrl: './node-tag-selector.component.html',
  styleUrls: ['./node-tag-selector.component.scss']
})
export class NodeTagSelectorComponent implements OnInit {

  @Input()
  nodes: NodeInfoDescription[];

  @Input()
  selectedTagsAfterUpdate: string[];

  @Output()
  createDynamicallySelectedTags: EventEmitter<NodeInfoDescription[]> = new EventEmitter<NodeInfoDescription[]>();

  @Output() emitSelectedNodeTags = new EventEmitter();

  filteredNodes: NodeInfoDescription[] = [];
  nodeTags: NodeTags[] = [];
  dynamicallySelectedTags: NodeTags["name"][] = [];

  ngOnInit(): void {
    console.log("here");
    if (this.nodes != undefined) {
      this.nodes.forEach(node => {
        if (node.supportedElements.length > 0 || node.registeredContainers.length > 0) {
          for (let tag of node.staticNodeMetadata.locationTags) {
            if (!this.nodeTags.some(n => n.name === tag)) {
              this.nodeTags.push({'name': tag, selected: false})
            }
          }
        }
      })
      if (this.selectedTagsAfterUpdate && this.selectedTagsAfterUpdate.length > 0) {
        this.selectedTagsAfterUpdate.forEach(oldTag => {
          this.nodeTags.forEach(entry => {
            if (entry.name === oldTag) {
              entry.selected = true;
            }
          })
        })
      }
    }
  }

  onSelectTag(tag: NodeTags) {
    tag.selected=!tag.selected
    this.dynamicallySelectedTags = [];
    for (let tag of this.nodeTags) {
      if(tag.selected){
        this.dynamicallySelectedTags.push(tag.name);
      }
    }
    this.filterAndEmitNodes();
  }

  filterAndEmitNodes() {
    this.filteredNodes = [];
     this.nodes.forEach(node => {
      node.staticNodeMetadata.locationTags.forEach(tag => {
        if (this.dynamicallySelectedTags.includes(tag)) {
          if (!this.filteredNodes.includes(node)) {
            this.filteredNodes.push(node);
          }
        }
      });
    });
    this.createDynamicallySelectedTags.emit(this.filteredNodes);
    this.emitSelectedNodeTags.emit(this.dynamicallySelectedTags);
  }
}
