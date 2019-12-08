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

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ProtocolDescription } from '../model/connect/grounding/ProtocolDescription';
import {ConfigurationInfo} from "../model/message/ConfigurationInfo";

@Component({
  selector: 'app-protocol',
  templateUrl: './protocol.component.html',
  styleUrls: ['./protocol.component.css']
})

export class ProtocolComponent {

  @Input() protocol: ProtocolDescription;
  @Input() selectedProtocol: ProtocolDescription;
  @Output() validateEmitter = new EventEmitter();
  @Output() editableEmitter = new EventEmitter();
  @Output() selectedProtocolEmitter = new EventEmitter();

  private hasConfig: Boolean;

  constructor() {
    this.hasConfig=true;
  }

  validateText(textValid) {

    // if(textValid && this.protocol.edit) {
        if(textValid) {
      this.validateEmitter.emit(true);
      this.selectedProtocol = this.protocol;
      this.selectedProtocolEmitter.emit(this.selectedProtocol);
    }
    else {
      this.validateEmitter.emit(false);
      this.selectedProtocol = null;
    }
  }
  ngOnInit() {

  }
}