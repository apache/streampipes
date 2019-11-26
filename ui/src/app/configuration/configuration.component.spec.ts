/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { ConfigurationComponent } from './configuration.component';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ConfigurationService } from './shared/configuration.service';
import { ConfigurationMockService } from './shared/configuration.test.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ConfigurationComponent', () => {

    let fixture: ComponentFixture<ConfigurationComponent>;
    let configurationComponent: ConfigurationComponent;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CommonModule,
                FlexLayoutModule
            ],
            declarations: [
                ConfigurationComponent
            ],
            providers: [
                { provide: ConfigurationService, useClass: ConfigurationMockService }
            ],
            schemas: [
                NO_ERRORS_SCHEMA
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(ConfigurationComponent);
        fixture.detectChanges();
        configurationComponent = fixture.componentInstance;
    }));

    it('should create the component', async(() => {
        expect(configurationComponent).toBeTruthy();
    }));

    it('should get two ConsulServices from service', async(() => {
        expect(configurationComponent.consulServices.length).toBe(2);
    }));

    /*
    it('should show two consul services from mock data', async(() => {
        expect(fixture.nativeElement.querySelectorAll('consul-service').length).toBe(2);
    }));
    it('should set width of both services equally', async(() => {
        expect(fixture.nativeElement.querySelectorAll('consul-service')[0].style.width).toBe(fixture.nativeElement.querySelectorAll('consul-service')[1].style.width);
    }));
    */
});
