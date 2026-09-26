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

import { TestBed } from '@angular/core/testing';
import { AuditService, UserService } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { of, Subject, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuditDialogComponent } from './audit-dialog.component';

describe('AuditDialogComponent', () => {
    const event = {
        eventId: 'id',
        actor: 'user-1',
        eventType: 'sp.adapter.create',
        outcome: 'succeeded',
        storedAt: '2026-09-25T12:00:00Z',
        resourceType: null,
        resourceId: null,
        locator: 'opaque',
    };
    const api = { getDetails: vi.fn(), getStatus: vi.fn() };
    const users = { getUserById: vi.fn() };
    beforeEach(() => {
        vi.resetAllMocks();
        api.getDetails.mockReturnValue(
            of({ event, recordedAt: event.storedAt, details: {} }),
        );
        api.getStatus.mockReturnValue(of({ queueDepth: 3 }));
        users.getUserById.mockReturnValue(of({ username: 'operator' }));
        TestBed.configureTestingModule({
            providers: [
                { provide: AuditService, useValue: api },
                { provide: UserService, useValue: users },
                { provide: DialogRef, useValue: { close: vi.fn() } },
            ],
        });
        TestBed.overrideComponent(AuditDialogComponent, {
            set: { template: '', imports: [] },
        });
    });
    it('resolves the current username independently of detail availability', () => {
        api.getDetails.mockReturnValue(throwError(() => ({ status: 404 })));
        const component =
            TestBed.createComponent(AuditDialogComponent).componentInstance;
        component.event = event;
        component.ngOnInit();
        expect(component.username).toBe('operator');
        expect(component.missing).toBe(true);
        expect(component.failed).toBe(false);
    });
    it('retains the recorded actor as fallback and cancels requests when closed', () => {
        users.getUserById.mockReturnValue(throwError(() => ({ status: 404 })));
        const pending = new Subject();
        api.getDetails.mockReturnValue(pending);
        const fixture = TestBed.createComponent(AuditDialogComponent);
        fixture.componentInstance.event = event;
        fixture.componentInstance.ngOnInit();
        expect(fixture.componentInstance.username).toBeUndefined();
        expect(fixture.componentInstance.event.actor).toBe('user-1');
        expect(pending.observed).toBe(true);
        fixture.destroy();
        expect(pending.observed).toBe(false);
    });
    it('fetches fresh status on open and refresh without fetching events or users', () => {
        const component =
            TestBed.createComponent(AuditDialogComponent).componentInstance;
        component.ngOnInit();
        component.load();
        expect(api.getStatus).toHaveBeenCalledTimes(2);
        expect(component.status?.queueDepth).toBe(3);
        expect(api.getDetails).not.toHaveBeenCalled();
        expect(users.getUserById).not.toHaveBeenCalled();
    });
});
