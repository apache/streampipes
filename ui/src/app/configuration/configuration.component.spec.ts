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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ConfigurationComponent } from './configuration.component';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ConfigurationService } from './shared/configuration.service';
import { ConfigurationMockService } from './shared/configuration.test.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ConfigurationComponent', () => {

  let fixture: ComponentFixture<ConfigurationComponent>;
  let configurationComponent: ConfigurationComponent;

  beforeEach(waitForAsync(() => {
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

  it('should create the component', waitForAsync(() => {
    expect(configurationComponent).toBeTruthy();
  }));

});
