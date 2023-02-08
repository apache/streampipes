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

import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'sp-schema-editor-header',
    templateUrl: './schema-editor-header.component.html',
    styleUrls: ['./schema-editor-header.component.scss'],
})
export class SchemaEditorHeaderComponent {
    @Input() countSelected: number;

    @Output() addNestedPropertyEmitter = new EventEmitter();
    @Output() addStaticValuePropertyEmitter = new EventEmitter();
    @Output() addTimestampPropertyEmitter = new EventEmitter();
    @Output() guessSchemaEmitter = new EventEmitter();
    @Output() updatePreviewEmitter = new EventEmitter();
    @Output() removeSelectedPropertiesEmitter = new EventEmitter();

    constructor() {}

    public addNestedProperty() {
        this.addNestedPropertyEmitter.emit();
    }

    public addStaticValueProperty() {
        this.addStaticValuePropertyEmitter.emit();
    }

    public addTimestampProperty() {
        this.addTimestampPropertyEmitter.emit();
    }

    public guessSchema() {
        this.guessSchemaEmitter.emit();
    }

    public removeSelectedProperties() {
        this.removeSelectedPropertiesEmitter.emit();
    }
}
