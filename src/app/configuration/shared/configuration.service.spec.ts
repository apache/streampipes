import { TestBed, async, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ConfigurationService } from './configuration.service';
import { ConsulService } from './consul-service.model';

describe('ConfigurationService', () => {

    let injector: TestBed;
    let service: ConfigurationService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule
            ],
            providers: [
                ConfigurationService
            ]
        });
        injector = getTestBed();
        service = injector.get(ConfigurationService);
        httpMock = injector.get(HttpTestingController);
    });
    afterEach(() => {
        httpMock.verify();
    });

    it('should create Get to /api/v2/consul', () => {
        service.getConsulServices().subscribe(res => res);
        const req = httpMock.expectOne('/streampipes-backend/api/v2/consul');
        expect(req.request.method).toBe('GET');
    });

    it('should create Post to /api/v2/consul', () => {
        service.updateConsulService({} as ConsulService).subscribe(res => res);
        const req = httpMock.expectOne('/streampipes-backend/api/v2/consul');
        expect(req.request.method).toBe('POST');
    });
});
