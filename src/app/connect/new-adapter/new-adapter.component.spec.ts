import {
  TestBed,
  async,
  ComponentFixture,
  getTestBed,
} from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NewAdapterComponent } from './new-adapter.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RestService } from '../rest.service';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatGridListModule } from '@angular/material';
import { CustomMaterialModule } from '../../CustomMaterial/custom-material.module';
import { DragulaModule } from 'ng2-dragula';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('NewComponent', () => {
  let fixture: ComponentFixture<NewAdapterComponent>;
  let newComponent: NewAdapterComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        DragulaModule,
        MatProgressSpinnerModule,
      ],
      declarations: [NewAdapterComponent],
      providers: [
        // { provide: RestService, useClass: EmptyMockService},
        // { provide: FormBuilder, useClass: EmptyMockService},
        // { provide: MatDialog, useClass: EmptyMockService}
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
    fixture = TestBed.createComponent(NewAdapterComponent);
    fixture.detectChanges();
    newComponent = fixture.componentInstance;
  }));

  // it('should create the component', async(() => {
  //     expect(newComponent).toBeTruthy();
  // }));

  // it('should get two ConsulServices from service', async(() => {
  //     expect(configurationComponent.consulServices.length).toBe(2);
  // }));

  /*
    it('should show two consul services from mock data', async(() => {
        expect(fixture.nativeElement.querySelectorAll('consul-service').length).toBe(2);
    }));
    it('should set width of both services equally', async(() => {
        expect(fixture.nativeElement.querySelectorAll('consul-service')[0].style.width).toBe(fixture.nativeElement.querySelectorAll('consul-service')[1].style.width);
    }));
    */
});
