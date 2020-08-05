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

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PipelineElementEndpointService} from "../../../platform-services/apis/pipeline-element-endpoint.service";

@Component({
  selector: 'endpoint-item',
  templateUrl: './endpoint-item.component.html',
  styleUrls: ['./endpoint-item.component.scss']
})
export class EndpointItemComponent implements OnInit {

  @Input()
  item: any;

  itemTypeTitle: string;
  itemTypeStyle: string;

  @Input()
  itemSelected: boolean;

  @Output()
  triggerInstallation: EventEmitter<any> = new EventEmitter<any>();

  constructor(private snackBar: MatSnackBar,
              private PipelineElementEndpointService: PipelineElementEndpointService) {

  }

  ngOnInit(): void {
   this.findItemTypeTitle();
   this.findItemStyle();
  }

  iconText(s) {
    var result = "";
    if (s.length <= 4) {
      result = s;
    } else {
      var words = s.split(" ");
      words.forEach(function (word, i) {
        if (i < 4) {
          result += word.charAt(0);
        }
      });
    }
    return result.toUpperCase();
  }

  getSelectedBackground() {
    if (this.itemSelected) return "#EEEEEE";
    else return "#FFFFFF";
  }

  findItemTypeTitle() {
    if (this.item.type === 'source') {
      this.itemTypeTitle = "Data Source";
    } else if (this.item.type === 'sepa') {
      this.itemTypeTitle = "Data Processor";
    } else {
      this.itemTypeTitle = "Data Sink";
    }
  }

  findItemStyle() {
    let baseType = "pe-label ";
    if (this.item.type == 'source') {
      this.itemTypeStyle = baseType + "source-label";
    } else if (this.item.type == 'sepa') {
      this.itemTypeStyle = baseType + "processor-label";
    } else {
      this.itemTypeStyle = baseType + "sink-label";
    }
  }

  installSingleElement(endpointItem) {
    let endpointItems = [];
    endpointItems.push(endpointItem);
    this.triggerInstallation.emit({endpointItems: endpointItems, install: true});
  }

  uninstallSingleElement(endpointItem) {
    let endpointItems = [];
    endpointItems.push(endpointItem);
    this.triggerInstallation.emit({endpointItems: endpointItems, install: false});
  }

  refresh(elementUri) {
    this.PipelineElementEndpointService.update(elementUri)
        .subscribe(msg => {
          console.log(msg);
          this.snackBar.open(msg.notifications[0].title, "Ok", {
            duration: 2000
          });
        })
        .add(() => {
          //this.loadCurrentElements(type);
        });
  }
}