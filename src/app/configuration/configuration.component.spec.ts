import { TestBed, async, ComponentFixture, getTestBed } from '@angular/core/testing';
import { ConfigurationComponent } from './configuration.component';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule, MatGridListModule, MatTooltipModule, MatCheckboxModule, MatIconModule, MatInputModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { ConsulServiceComponent } from './consul-service/consul-service.component';
import { ConfigurationService } from './shared/configuration.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('ConfigurationModule', () => {

    let fixture: ComponentFixture<ConfigurationComponent>;
    let configurationComponent: ConfigurationComponent;
    let injector: TestBed;
    let service: ConfigurationService;
    let httpMock: HttpTestingController;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CommonModule,
                FlexLayoutModule,
                MatGridListModule,
                MatButtonModule,
                MatIconModule,
                MatInputModule,
                MatCheckboxModule,
                MatTooltipModule,
                FormsModule,
                HttpClientTestingModule
            ],
            declarations: [
                ConfigurationComponent
            ],
            providers: [
                ConfigurationService
            ],
            schemas: [CUSTOM_ELEMENTS_SCHEMA]
        }).compileComponents();
        injector = getTestBed();
        service = injector.get(ConfigurationService);
        httpMock = injector.get(HttpTestingController);
        fixture = TestBed.createComponent(ConfigurationComponent);
        configurationComponent = fixture.componentInstance;
    }));

    it('should create the component', async(() => {
        expect(configurationComponent).toBeTruthy();
    }));
    it('should get consul configurations', async(() => {
        const req = httpMock.expectOne('/streampipes-backend/api/v2/consul');
        expect(req.request.method).toBe('GET');
        /*
        req.flush([
            {
                "mainKey": "sp/test/1",
                "meta": {
                    status: "passing"
                },
                "name": "test1",
                "configs": [
                    {
                        "key": "testint",
                        "value": "80",
                        "valueType": "xs:integer",
                        "isPassword": false,
                        "description": "test int"
                    },
                    {
                        "key": "teststring",
                        "value": "765",
                        "valueType": "xs:string",
                        "isPassword": false,
                        "description": "test string"
                    }
                ]
            },
            {
                "mainKey": "sp/test/2",
                "meta": {
                    status: "critical"
                },
                "name": "test2",
                "configs": [
                    {
                        "key": "testbool",
                        "value": "false",
                        "valueType": "xs:boolean",
                        "isPassword": false,
                        "description": "test bool"
                    }
                ]
            }
        ]);
        console.log(fixture.nativeElement);
        expect(fixture.nativeElement.querySelector('consul-service')).toBeTruthy();
        */
    }));
});
