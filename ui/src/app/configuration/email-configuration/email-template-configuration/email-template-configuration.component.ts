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

import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import {
    EmailTemplate,
    MailConfigService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-email-template-configuration',
    templateUrl: './email-template-configuration.component.html',
    styleUrls: ['./email-template-configuration.component.scss'],
})
export class SpEmailTemplateConfigurationComponent implements OnInit {
    template: EmailTemplate;
    originalTemplate: string;
    templateLoaded = false;
    templateStored = false;

    editorOptions = {
        mode: 'html',
        autoRefresh: true,
        theme: 'dracula',
        autoCloseBrackets: true,
        lineNumbers: true,
        lineWrapping: true,
        gutters: ['CodeMirror-lint-markers'],
        lint: true,
    };

    allowedPlaceholders: { placeholder: string; description: string }[] = [
        { placeholder: '###LOGO###', description: 'The default logo' },
        { placeholder: '###BASE_URL###', description: 'The base URL' },
        { placeholder: '###TITLE###', description: 'Email title' },
        { placeholder: '###PREHEADER###', description: 'Email preheader' },
        {
            placeholder: '###INNER###',
            description: 'Email custom inner content (mandatory)',
        },
    ];

    constructor(
        private fb: UntypedFormBuilder,
        private mailConfigService: MailConfigService,
    ) {}

    ngOnInit(): void {
        this.loadTemplate();
    }

    loadTemplate(): void {
        this.templateLoaded = false;
        this.mailConfigService.getMailTemplate().subscribe(template => {
            this.originalTemplate = template.template;
            this.template = template;
            this.templateLoaded = true;
        });
    }

    restoreTemplate(): void {
        this.template.template = this.originalTemplate;
    }

    saveTemplate(): void {
        this.templateStored = false;
        this.mailConfigService
            .updateMailTemplate(this.template)
            .subscribe(() => {
                this.templateStored = true;
                this.loadTemplate();
            });
    }
}
