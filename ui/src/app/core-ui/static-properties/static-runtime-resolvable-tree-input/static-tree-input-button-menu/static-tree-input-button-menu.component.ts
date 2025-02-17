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
    selector: 'sp-static-tree-input-button-menu',
    templateUrl: './static-tree-input-button-menu.component.html',
    styleUrl: './static-tree-input-button-menu.component.scss',
})
export class StaticTreeInputButtonMenuComponent {
    @Input()
    showOptions: boolean;
    @Input()
    loading: boolean;
    @Input()
    editorMode: 'tree' | 'text';

    @Output()
    resetOptionsAndReload = new EventEmitter<void>();
    @Output()
    reload = new EventEmitter<void>();
    @Output()
    selectedEditorModeEmitter = new EventEmitter<'tree' | 'text'>();

    onResetOptionsAndReload() {
        this.resetOptionsAndReload.emit();
    }

    onReload() {
        this.reload.emit();
    }

    onChangeEditor(mode: 'tree' | 'text') {
        this.selectedEditorModeEmitter.emit(mode);
    }
}
