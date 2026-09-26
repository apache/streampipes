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

import { TranslateService } from '@ngx-translate/core';
import { AuditDialogComponent } from './audit-dialog.component';
import { TestBed } from '@angular/core/testing';
import { AuditService, AuditEntry } from '@streampipes/platform-services';
import {
    SpBreadcrumbService,
    DialogService,
    FeatureCardService,
} from '@streampipes/shared-ui';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuditLogComponent } from './audit-log.component';

describe('AuditLogComponent', () => {
    const event: AuditEntry = {
        eventId: 'event-1',
        storedAt: '2026-09-25T12:00:00.000000123Z',
        eventType: 'sp.adapter.create',
        actor: 'user-1',
        outcome: 'succeeded',
        resourceType: 'adapter',
        resourceId: '1',
        locator: 'opaque',
    };
    const api = {
        getStatus: vi.fn(),
        getEventTypes: vi.fn(),
        getEvents: vi.fn(),
        getDetails: vi.fn(),
    };
    const cards = { supportsFeatureCard: vi.fn(), openFeatureCard: vi.fn() };
    const dialogs = { open: vi.fn() };
    let component: AuditLogComponent;
    beforeEach(() => {
        vi.resetAllMocks();
        cards.supportsFeatureCard.mockImplementation(
            type => type === 'adapter',
        );
        api.getStatus.mockReturnValue(of({ enabled: true }));
        api.getEventTypes.mockReturnValue(of(['sp.adapter.create']));
        api.getEvents.mockReturnValue(
            of({ items: [event], nextCursor: 'next' }),
        );
        TestBed.configureTestingModule({
            providers: [
                { provide: FeatureCardService, useValue: cards },
                { provide: DialogService, useValue: dialogs },
                {
                    provide: TranslateService,
                    useValue: { instant: (key: string) => key },
                },
                { provide: AuditService, useValue: api },
                {
                    provide: SpBreadcrumbService,
                    useValue: { updateBreadcrumb: vi.fn() },
                },
            ],
        });
        TestBed.overrideComponent(AuditLogComponent, {
            set: { template: '', imports: [] },
        });
        component =
            TestBed.createComponent(AuditLogComponent).componentInstance;
    });

    it('only previews resources with both fields and a registered card', () => {
        expect(
            component.resourcePreview({
                ...event,
                resourceType: null,
                resourceId: null,
            }),
        ).toBeUndefined();
        expect(
            component.resourcePreview({
                ...event,
                resourceType: undefined,
                resourceId: undefined,
            }),
        ).toBeUndefined();
        expect(
            component.resourcePreview({ ...event, resourceType: null }),
        ).toBeUndefined();
        expect(
            component.resourcePreview({ ...event, resourceId: null }),
        ).toBeUndefined();
        expect(
            component.resourcePreview({ ...event, resourceType: 'unknown' }),
        ).toBeUndefined();
        expect(
            component.resourcePreview({
                ...event,
                resourceId: 'urn:adapter:123',
            }),
        ).toEqual({ type: 'adapter', id: 'urn:adapter:123' });
        cards.supportsFeatureCard.mockImplementation(
            type => type === 'bytefabrik-notebook',
        );
        expect(
            component.resourcePreview({
                ...event,
                resourceType: 'bytefabrik-notebook',
            }),
        ).toEqual({ type: 'bytefabrik-notebook', id: '1' });
    });

    it('opens the registered card without triggering the audit details row action', () => {
        const click = new Event('click');
        const stop = vi.spyOn(click, 'stopPropagation');
        component.openResourcePreview(event, click);
        expect(stop).toHaveBeenCalled();
        expect(cards.openFeatureCard).toHaveBeenCalledWith('adapter', '1');
        expect(dialogs.open).not.toHaveBeenCalled();
    });

    it('keeps time bounds stable across pages and resets cursors when filters change', () => {
        component.ngOnInit();
        const first = api.getEvents.mock.calls[0][0];
        component.next();
        expect(api.getEvents.mock.calls[1][0]).toEqual({
            ...first,
            cursor: 'next',
        });
        component.previous();
        expect(api.getEvents.mock.calls[2][0]).toEqual(first);
        component.actor = 'user-2';
        component.apply();
        expect(api.getEvents.mock.calls[3][0].actor).toBe('user-2');
        expect(api.getEvents.mock.calls[3][0].cursor).toBeUndefined();
        expect(component.page).toBe(0);
    });

    it('opens details and status as separate slide-in dialogs without loading details in the table', () => {
        component.ngOnInit();
        expect(api.getDetails).not.toHaveBeenCalled();
        component.showDetails(event);
        expect(dialogs.open).toHaveBeenCalledWith(
            AuditDialogComponent,
            expect.objectContaining({ data: { event } }),
        );
        component.showStatus();
        expect(dialogs.open).toHaveBeenLastCalledWith(
            AuditDialogComponent,
            expect.objectContaining({ title: 'Audit status' }),
        );
    });

    it('distinguishes storage failure from empty results and allows retry', () => {
        api.getEvents.mockReturnValueOnce(throwError(() => ({ status: 503 })));
        component.ngOnInit();
        expect(component.failed).toBe(true);
        expect(component.loading).toBe(false);
        component.retry();
        expect(component.failed).toBe(false);
        expect(component.dataSource.data).toEqual([event]);
    });

    it('does not query storage when auditing is disabled', () => {
        api.getStatus.mockReturnValue(of({ enabled: false }));
        component.ngOnInit();
        expect(api.getEvents).not.toHaveBeenCalled();
    });
});
