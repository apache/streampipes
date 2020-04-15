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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormatDescription} from '../model/connect/grounding/FormatDescription';

@Component({
    selector: 'app-format-list',
    templateUrl: './format-list.component.html',
    styleUrls: ['./format-list.component.css']
  })

export class FormatListComponent {
    @Input() selectedFormat: FormatDescription;
    @Input() allFormats: FormatDescription[];    
    @Output() validateEmitter = new EventEmitter();
    @Output() selectedFormatEmitter = new EventEmitter();

    constructor() {
      
    }
    formatEditable(selectedFormat) {

      this.allFormats.forEach(format => {
        if(format!=selectedFormat){
          format.edit = false;
        }
      });

    }

    formatSelected(selectedFormat) {
      this.selectedFormat = selectedFormat;
      this.selectedFormatEmitter.emit(this.selectedFormat)

    }

    validateAll(allValid) {
      this.validateEmitter.emit(allValid);
    }
    ngOnInit(){
    }

  }