import { TestBed, async, ComponentFixture, getTestBed } from '@angular/core/testing';
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
