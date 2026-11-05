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

import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';
import { PipelineSummaryDto } from '@streampipes/platform-services';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PipelineOverviewCardComponent } from './pipeline-overview-card.component';

@Component({
    imports: [PipelineOverviewCardComponent],
    template: `
        <sp-pipeline-overview-card
            [pipeline]="pipeline()"
            icon="play_arrow"
            accentColor="rgb(1, 2, 3)"
            [hasWritePrivileges]="hasWritePrivileges()"
            [operationInProgress]="operationInProgress()"
            (startPipeline)="startPipeline()"
            (stopPipeline)="stopPipeline()"
            (showDetails)="showDetails()"
            (editPipeline)="editPipeline()"
            (showNotifications)="showNotifications()"
        >
            <button id="preview" cardHeaderActions>Preview</button>
            <span id="asset" cardAssetContext>Hall 3</span>
        </sp-pipeline-overview-card>
    `,
})
class PipelineOverviewCardHostComponent {
    pipeline = signal({
        elementId: 'p1',
        name: 'Pump flow monitoring',
        description: 'Detects flow rate anomalies',
        createdAt: new Date(2026, 2, 12, 9, 41).getTime(),
        running: true,
        healthStatus: 'OK',
        pipelineNotifications: [],
        valid: true,
    } as PipelineSummaryDto);
    hasWritePrivileges = signal(true);
    operationInProgress = signal(false);
    startPipeline = vi.fn();
    stopPipeline = vi.fn();
    showDetails = vi.fn();
    editPipeline = vi.fn();
    showNotifications = vi.fn();
}

describe('PipelineOverviewCardComponent', () => {
    let fixture: ComponentFixture<PipelineOverviewCardHostComponent>;
    let host: PipelineOverviewCardHostComponent;

    const card = () => fixture.nativeElement as HTMLElement;
    const byCy = (id: string) =>
        card().querySelector<HTMLButtonElement>(`[data-cy="${id}"]`);
    const text = (id: string) => byCy(id)?.textContent.trim();

    async function update(changes: Partial<PipelineSummaryDto>) {
        host.pipeline.update(pipeline => ({
            ...pipeline,
            ...changes,
        })) as void;
        await fixture.whenStable();
        fixture.detectChanges();
    }

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [PipelineOverviewCardHostComponent],
            providers: [provideTranslateService()],
        }).compileComponents();
        fixture = TestBed.createComponent(PipelineOverviewCardHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('shows identity, status and facts', () => {
        expect(card().querySelector('.card-title-link').textContent).toContain(
            'Pump flow monitoring',
        );
        expect(text('pipeline-card-status')).toBe('Running');
        expect(text('pipeline-card-no-notifications')).toBe('None');
        expect(text('pipeline-card-created')).toBe('12.03.2026');
    });

    it('shows the module icon', () => {
        expect(card().querySelector('.card-leading mat-icon').textContent).toBe(
            'play_arrow',
        );
    });

    it('projects the table actions and asset context', () => {
        expect(
            card().querySelector('.card-header-actions #preview'),
        ).not.toBeNull();
        expect(card().querySelector('.card-context #asset')).not.toBeNull();
    });

    it('opens the details from the title and edits from the footer', () => {
        card().querySelector<HTMLElement>('.card-title-link').click();
        byCy('pipeline-card-edit').click();

        expect(host.showDetails).toHaveBeenCalledOnce();
        expect(host.editPipeline).toHaveBeenCalledOnce();
    });

    it('offers stop for a running pipeline', () => {
        expect(byCy('pipeline-card-start')).toBeNull();

        byCy('pipeline-card-stop').click();

        expect(host.stopPipeline).toHaveBeenCalledOnce();
    });

    it('offers start for a stopped pipeline', async () => {
        await update({ running: false });

        expect(text('pipeline-card-status')).toBe('Stopped');
        expect(byCy('pipeline-card-stop')).toBeNull();

        byCy('pipeline-card-start').click();

        expect(host.startPipeline).toHaveBeenCalledOnce();
    });

    it('shows the health of a running pipeline', async () => {
        await update({ healthStatus: 'FAILURE' });

        expect(text('pipeline-card-status')).toBe('Failed');
    });

    it('opens the notifications from their count', async () => {
        await update({ pipelineNotifications: ['first', 'second'] });

        expect(text('pipeline-card-notifications')).toContain('2');
        expect(byCy('pipeline-card-no-notifications')).toBeNull();

        byCy('pipeline-card-notifications').click();

        expect(host.showNotifications).toHaveBeenCalledOnce();
    });

    it('explains an invalid pipeline and prevents starting it', async () => {
        await update({ running: false, valid: false as true });

        expect(byCy('pipeline-card-invalid')).not.toBeNull();
        expect(byCy('pipeline-card-start').disabled).toBe(true);
    });

    it('blocks actions while an operation is in progress', async () => {
        host.operationInProgress.set(true);
        await fixture.whenStable();
        fixture.detectChanges();

        expect(byCy('pipeline-card-stop').disabled).toBe(true);
    });

    it('keeps read-only users from changing the pipeline', async () => {
        host.hasWritePrivileges.set(false);
        await update({ pipelineNotifications: ['first'] });

        expect(byCy('pipeline-card-stop').disabled).toBe(true);
        expect(byCy('pipeline-card-edit')).toBeNull();
        expect(byCy('pipeline-card-notifications').disabled).toBe(true);
    });
});
