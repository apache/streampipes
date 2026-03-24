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

import { Component, Input, OnInit, inject } from '@angular/core';
import { PipelineElementService } from '@streampipes/platform-services';
import { Lexer, Parser } from 'marked';
import { MarkdownComponent } from 'ngx-markdown';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-pipeline-element-documentation',
    templateUrl: './pipeline-element-documentation.component.html',
    styleUrls: ['./pipeline-element-documentation.component.scss'],
    imports: [MarkdownComponent, NgClass, ClassDirective, TranslatePipe],
})
export class PipelineElementDocumentationComponent implements OnInit {
    private pipelineElementService = inject(PipelineElementService);

    @Input()
    appId: string;

    @Input()
    useStyling: boolean;

    documentationMarkdown: any;
    error: any;

    ngOnInit(): void {
        this.pipelineElementService.getDocumentation(this.appId).subscribe({
            next: msg => {
                this.error = false;
                this.documentationMarkdown = this.compileMarkdown(msg);
            },
            error: () => {
                this.error = true;
            },
        });
    }

    compileMarkdown(value: string): string {
        return new Parser().parse(new Lexer().lex(value));
    }
}
