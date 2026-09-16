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
import { TranslateModule } from '@ngx-translate/core';
import { describe, expect, it } from 'vitest';
import { SchemaPreviewStatusComponent } from './schema-preview-status.component';

describe('Schema preview status', () => {
    it('prioritizes disabled, running, and failed states over preview freshness', () => {
        TestBed.configureTestingModule({
            imports: [SchemaPreviewStatusComponent, TranslateModule.forRoot()],
        });
        const fixture = TestBed.createComponent(SchemaPreviewStatusComponent);
        const status = () => {
            fixture.detectChanges();
            return fixture.nativeElement.querySelector(
                '.schema-status',
            ) as HTMLElement;
        };
        fixture.componentRef.setInput('active', false);
        fixture.componentRef.setInput('running', true);
        fixture.componentRef.setInput('error', true);
        fixture.componentRef.setInput('outdated', true);
        expect(status().textContent).toContain('Transformation disabled');
        fixture.componentRef.setInput('active', true);
        expect(status().classList.contains('status-info')).toBe(true);
        expect(status().textContent).toContain('Running script');
        fixture.componentRef.setInput('running', false);
        expect(status().classList.contains('status-error')).toBe(true);
        fixture.componentRef.setInput('error', false);
        expect(status().classList.contains('status-warning')).toBe(true);
        expect(status().textContent).toContain('Preview is out of date.');
        fixture.componentRef.setInput('outdated', false);
        expect(status().classList.contains('status-success-bg')).toBe(true);
        expect(status().textContent).toContain('check_circle');
        expect(status().textContent).toContain('Preview is up to date');
        fixture.destroy();
    });

    it('keeps the editor status concise without a tinted background', () => {
        TestBed.configureTestingModule({
            imports: [SchemaPreviewStatusComponent, TranslateModule.forRoot()],
        });
        const fixture = TestBed.createComponent(SchemaPreviewStatusComponent);
        fixture.componentRef.setInput('inline', true);
        fixture.componentRef.setInput('outdated', true);
        fixture.detectChanges();
        const status = fixture.nativeElement.querySelector(
            '.schema-status',
        ) as HTMLElement;
        expect(status.textContent).toContain(
            'Run the script to update the preview.',
        );
        expect(status.classList.contains('status-warning')).toBe(true);
        expect(status.classList.contains('status-warning-bg')).toBe(false);
        fixture.destroy();
    });
});
