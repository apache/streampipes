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

import { Component, inject, OnInit } from '@angular/core';
import { FormsModule, UntypedFormBuilder } from '@angular/forms';
import {
    EmailTemplate,
    MailConfigService,
} from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatButton } from '@angular/material/button';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import type { editor as MonacoEditor } from 'monaco-editor';

@Component({
    selector: 'sp-email-template-configuration',
    templateUrl: './email-template-configuration.component.html',
    styleUrls: ['./email-template-configuration.component.scss'],
    imports: [
        FlexDirective,
        SplitSectionComponent,
        LayoutDirective,
        LayoutAlignDirective,
        MatButton,
        SpAlertBannerComponent,
        LayoutGapDirective,
        MonacoEditorModule,
        FormsModule,
        TranslatePipe,
    ],
})
export class SpEmailTemplateConfigurationComponent implements OnInit {
    private fb = inject(UntypedFormBuilder);
    private mailConfigService = inject(MailConfigService);

    template: EmailTemplate;
    originalTemplate: string;
    templateLoaded = false;
    templateStored = false;
    private translateService = inject(TranslateService);

    editorOptions: MonacoEditor.IStandaloneEditorConstructionOptions = {
        language: 'html',
        theme: 'vs-dark',
        lineNumbers: 'on',
        wordWrap: 'on',
        automaticLayout: true,
        scrollBeyondLastLine: false,
        minimap: { enabled: false },
    };

    allowedPlaceholders: { placeholder: string; description: string }[] = [
        {
            placeholder: '###LOGO###',
            description: this.translateService.instant('The default logo'),
        },
        {
            placeholder: '###BASE_URL###',
            description: this.translateService.instant('The base URL'),
        },
        {
            placeholder: '###TITLE###',
            description: this.translateService.instant('Email title'),
        },
        {
            placeholder: '###PREHEADER###',
            description: this.translateService.instant('Email preheader'),
        },
        {
            placeholder: '###INNER###',
            description: this.translateService.instant(
                'Email custom inner content (mandatory)',
            ),
        },
    ];

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
