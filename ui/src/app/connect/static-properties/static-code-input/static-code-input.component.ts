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

import {CodeInputStaticProperty} from "../../../core-model/gen/streampipes-model";
import {AbstractValidatedStaticPropertyRenderer} from "../base/abstract-validated-static-property";
import {AfterViewInit, Component, OnInit} from "@angular/core";

import 'codemirror/mode/javascript/javascript';
import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/javascript-hint';
import 'codemirror/addon/lint/javascript-lint';
import 'codemirror/addon/lint/lint';
import {JSHINT} from 'jshint';
import * as CodeMirror from "codemirror";

(<any>window).JSHINT = JSHINT;

@Component({
  selector: 'app-static-code-input',
  templateUrl: './static-code-input.component.html',
  styleUrls: ['./static-code-input.component.scss']
})
export class StaticCodeInputComponent
    extends AbstractValidatedStaticPropertyRenderer<CodeInputStaticProperty> implements OnInit, AfterViewInit {

  editorOptions = {
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
  };

  constructor() {
    super();
  }

  ngOnInit() {
    if (!this.staticProperty.value || this.staticProperty.value === "") {
      this.staticProperty.value = this.staticProperty.codeTemplate;
    }
  }

  ngAfterViewInit() {
    this.enableCodeHints();
  }

  onStatusChange(status: any) {
  }

  onValueChange(value: any) {
  }

  resetCode() {
    this.staticProperty.value = this.staticProperty.codeTemplate;
  };

  enableCodeHints() {
    var jsHint = (CodeMirror as any).hint.javascript;
    (CodeMirror as any).hint.javascript = (cm) => {
      let cursor = cm.getCursor();
      let token = cm.getTokenAt(cursor);
      let inner = {from: cm.getCursor(), to: cm.getCursor(), list: []};
      let previousCursor = {line: cursor.line, ch: (cursor.ch - 1), sticky: null}
      let previousToken = cm.getTokenAt(previousCursor);
      if (token.string === "." && previousToken.string === "event") {
        this.eventSchemas[0].eventProperties.forEach(ep => {
          inner.list.unshift(ep.runtimeName);
        })
      } else {
        inner = jsHint(cm);
      }
      return inner;
    };
  }


}