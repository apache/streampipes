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
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { RuntimeResolvableTreeInputStaticProperty } from '@streampipes/platform-services';

@Component({
    selector: 'sp-static-tree-input-text-editor',
    templateUrl: './static-tree-input-text-editor.component.html',
})
export class StaticTreeInputTextEditorComponent implements OnInit {
    @Input()
    staticProperty: RuntimeResolvableTreeInputStaticProperty;

    @Output()
    performValidationEmitter: EventEmitter<void> = new EventEmitter<void>();

    editorOptions = {
        mode: 'text/plain',
        autoRefresh: true,
        theme: 'dracula',
        lineNumbers: true,
        lineWrapping: true,
        readOnly: false,
        extraKeys: {
            'Ctrl-Space': 'autocomplete',
        },
    };

    headerText =
        '# Provide OPC UA Node IDs below, one per line.\n' +
        '# Format: ns=<namespace>;s=<node_id> (e.g., ns=3;s=SampleNodeId)\n';
    textEditor: string = '';

    private textChangeSubject: Subject<string> = new Subject<string>();

    constructor() {
        this.textChangeSubject.pipe(debounceTime(500)).subscribe(value => {
            this.onTextChange(value);
        });
    }

    ngOnInit() {
        this.textEditor =
            this.headerText +
            this.staticProperty.selectedNodesInternalNames.join('\n');
    }

    onTextEditorChange(value: string): void {
        this.textChangeSubject.next(value);
    }

    onTextChange(value: string): void {
        this.staticProperty.selectedNodesInternalNames = value
            .split('\n')
            // remove empty lines and comments starting with #
            .filter(line => line.trim() !== '' && !line.startsWith('#'));
        this.performValidationEmitter.emit();
    }
}
