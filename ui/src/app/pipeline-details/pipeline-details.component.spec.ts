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

import { Component, Input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';
import {
    Pipeline,
    PipelineCanvasMetadataService,
    PipelineMonitoringService,
    PipelineService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogService,
    KeyboardShortcutService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../services/auth.service';
import { PipelineOperationsService } from '../pipelines/services/pipeline-operations.service';
import { SpPipelineDetailsComponent } from './pipeline-details.component';
import { PipelinePreviewComponent } from './components/preview/pipeline-preview.component';
import { PipelineDetailsExpansionPanelComponent } from './components/pipeline-details-expansion-panel/pipeline-details-expansion-panel.component';

@Component({
    selector: 'sp-pipeline-preview',
    template: '<ng-content />',
})
class PreviewStubComponent {
    @Input() metricsInfo: unknown;
    @Input() pipeline: Pipeline;
    @Input() pipelineCanvasMetadata: unknown;
}

@Component({
    selector: 'sp-pipeline-details-expansion-panel',
    template: '',
})
class InspectorStubComponent {
    @Input() hasWritePipelinePrivileges: boolean;
    @Input() logInfo: unknown;
    @Input() pipeline: Pipeline;
}

describe('Pipeline details workspace', () => {
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SpPipelineDetailsComponent, TranslateModule.forRoot()],
            providers: [
                provideRouter([]),
                ...[
                    ActivatedRoute,
                    PipelineService,
                    PipelineCanvasMetadataService,
                    PipelineMonitoringService,
                    AuthService,
                    CurrentUserService,
                    SpBreadcrumbService,
                    DialogService,
                    KeyboardShortcutService,
                    PipelineOperationsService,
                ].map(provide => ({ provide, useValue: {} })),
                {
                    provide: KeyboardShortcutService,
                    useValue: { register: () => ({ unregister: () => {} }) },
                },
                { provide: CurrentUserService, useValue: { user$: EMPTY } },
            ],
        })
            .overrideComponent(SpPipelineDetailsComponent, {
                remove: {
                    imports: [
                        PipelinePreviewComponent,
                        PipelineDetailsExpansionPanelComponent,
                    ],
                },
                add: {
                    imports: [PreviewStubComponent, InspectorStubComponent],
                },
            })
            .compileComponents();
    });

    function render(available = true, canWrite = false, notFound = false) {
        const fixture = TestBed.createComponent(SpPipelineDetailsComponent);
        const component = fixture.componentInstance;
        component.pipeline = available
            ? ({ name: 'Packaging line', running: true } as Pipeline)
            : undefined;
        component.pipelineAvailable = available;
        component.pipelineNotFound = notFound;
        component.hasPipelineWritePrivileges = canWrite;
        fixture.detectChanges();
        return fixture;
    }

    it('renders identity and monitoring controls for read-only users', () => {
        const root: HTMLElement = render().nativeElement;
        expect(root.querySelector('h1')?.textContent).toContain(
            'Packaging line',
        );
        expect(
            root
                .querySelector('sp-page-header')
                ?.classList.contains('page-header-workspace'),
        ).toBe(true);
        expect(
            root.querySelector('.page-header__identity [role="status"]')
                ?.textContent,
        ).toContain('Running');
        expect(
            root.querySelector('[data-cy="pipeline-details-view-code"]'),
        ).toBeNull();
        expect(
            root.querySelector('[data-cy="options-edit-pipeline"]'),
        ).toBeNull();
        expect(
            root.querySelector('[data-cy="pipeline-details-options"]'),
        ).toBeNull();
        expect(
            root.querySelector(
                'sp-workspace-container .workspace-container__toolbar sp-pipeline-details-toolbar',
            ),
        ).not.toBeNull();
        expect(
            root.querySelector(
                'sp-workspace-container .workspace-container__content sp-pipeline-preview',
            ),
        ).not.toBeNull();
    });

    it('keeps edit, code view, and refresh connected to existing handlers', () => {
        const fixture = render(true, true);
        const edit = vi
            .spyOn(fixture.componentInstance, 'editPipeline')
            .mockImplementation(() => {});
        const code = vi
            .spyOn(fixture.componentInstance, 'openPipelineAsCodeDialog')
            .mockImplementation(() => {});
        fixture.nativeElement
            .querySelector(
                'sp-pipeline-details-toolbar [data-cy="pipeline-details-view-code"]',
            )
            .click();
        expect(code).toHaveBeenCalledOnce();
        const refresh = vi
            .spyOn(fixture.componentInstance, 'triggerReload')
            .mockImplementation(() => {});
        fixture.nativeElement
            .querySelector('[data-cy="options-edit-pipeline"]')
            .click();
        fixture.nativeElement
            .querySelector('[aria-label="Refresh metrics"]')
            .click();
        expect(edit).toHaveBeenCalledOnce();
        expect(refresh).toHaveBeenCalledOnce();
    });

    it('shows loading without exposing pipeline actions', () => {
        const fixture = render(false, true);
        expect(
            fixture.nativeElement.querySelector('sp-spinner'),
        ).not.toBeNull();
        expect(
            fixture.nativeElement.querySelector(
                '[data-cy="options-edit-pipeline"]',
            ),
        ).toBeNull();
    });

    it('keeps back navigation available for a missing pipeline', () => {
        const fixture = render(false, true, true);
        expect(fixture.nativeElement.textContent).toContain(
            'The desired pipeline was not found!',
        );
        expect(
            fixture.nativeElement.querySelector('[aria-label="Back"]'),
        ).not.toBeNull();
        expect(
            fixture.nativeElement.querySelector('sp-pipeline-details-toolbar'),
        ).toBeNull();
    });
});
