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

import CodeMirror from 'codemirror';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/javascript-hint';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/javascript-lint';
import {JSHINT} from 'jshint';

export class CodeEditorDirective {

    require: string = "?ngModel";
    replace: boolean = true;
    transclude: boolean = true;
    template: string = '<div class="code-editor"></div>';

    constructor() {
        (<any>window).JSHINT = JSHINT;
    }

    link(scope, element, attrs, ngModelCtrl, transclude) {
        let selectedElement = scope.$eval(attrs.inputStreams);
        scope.editor = CodeMirror(element[0], {
            mode: "javascript",
            autoRefresh: true,
            theme: 'dracula',
            autoCloseBrackets: true,
            lineNumbers: true,
            lineWrapping: true,
            gutters: ["CodeMirror-lint-markers"],
            lint: true,
            extraKeys: {
                "Ctrl-Space": "autocomplete"
            }
        });
        this.enableCodeHints(selectedElement);

        if (ngModelCtrl) {
            ngModelCtrl.$render = () => {
                setTimeout(() => {
                    scope.editor.setValue(ngModelCtrl.$viewValue);
                    scope.editor.refresh();
                },200);
            }
        }
        scope.editor.on('change', () => {
            ngModelCtrl.$setViewValue(scope.editor.getValue());
            scope.$emit('editor-change')
        })
    }

    enableCodeHints(selectedElement: any) {
        var jsHint = CodeMirror.hint.javascript;
        CodeMirror.hint.javascript = (cm) => {
            let cursor = cm.getCursor();
            let token = cm.getTokenAt(cursor);
            let inner = {from: cm.getCursor(), to: cm.getCursor(), list: []};
            let previousCursor = {line: cursor.line, ch: (cursor.ch - 1), sticky: null}
            let previousToken = cm.getTokenAt(previousCursor);
            if (token.string === "." && previousToken.string === "event") {
                selectedElement.inputStreams[0].eventSchema.eventProperties.forEach(ep => {
                    inner.list.unshift(ep.properties.runtimeName);
                })
            } else {
                inner = jsHint(cm);
            }
            return inner;
        };
    }
}