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

import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ServiceConfigsPasswordComponent } from './service-configs-password.component';
import { ConfigurationService } from '../../../../shared/configuration.service';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';

describe('ConsulConfigsPasswordComponent', () => {
    let fixture: ComponentFixture<ServiceConfigsPasswordComponent>;

    let configurationServiceStub: Partial<ConfigurationService>;

    let component: ServiceConfigsPasswordComponent;

    let configurationServcie: ConfigurationService;

    beforeEach(waitForAsync(() => {
        configurationServiceStub = {
            adjustConfigurationKey(consulKey) {
                let str1 = consulKey.replace(/SP/g, '');
                str1 = str1.replace(/_/g, ' ');
                if (str1.startsWith(' ')) {
                    str1 = str1.slice(1, str1.length);
                }
                return str1;
            },
        };

        TestBed.configureTestingModule({
            imports: [
                CommonModule,
                BrowserAnimationsModule,
                FlexLayoutModule,
                MatGridListModule,
                MatButtonModule,
                MatIconModule,
                MatInputModule,
                MatCheckboxModule,
                MatTooltipModule,
                FormsModule,
                HttpClientTestingModule,
            ],
            declarations: [ServiceConfigsPasswordComponent],
            providers: [
                {
                    provide: ConfigurationService,
                    useValue: configurationServiceStub,
                },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(ServiceConfigsPasswordComponent);

        configurationServcie =
            fixture.debugElement.injector.get(ConfigurationService);

        component = fixture.componentInstance;
    }));

    it(`should create`, waitForAsync(() => {
        expect(component).toBeTruthy();
    }));

    it('should show pw', waitForAsync(() => {
        expect(component.password).toBe('*****');
    }));

    it(`should click button`, waitForAsync(() => {
        spyOn(component, 'changePw');

        const input = fixture.debugElement.nativeElement.querySelector('input');
        input.click();

        fixture.whenStable().then(() => {
            expect(component.changePw).toHaveBeenCalled();
            const bannerDe: DebugElement = fixture.debugElement;

            const inputDe = bannerDe.query(By.css('input'));
            const inputValue: HTMLElement = inputDe.nativeElement;

            expect(inputValue.textContent).toBe('');
        });
    }));
});
