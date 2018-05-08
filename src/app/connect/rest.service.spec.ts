import { TestBed, async, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RestService } from './rest.service';
import { AdapterDescription } from './model/AdapterDescription';

describe('ConnectModuleRestService', () => {

    let injector: TestBed;
    let service: RestService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule
            ],
            providers: [
                RestService
            ]
        });
        injector = getTestBed();
        service = injector.get(RestService);
        httpMock = injector.get(HttpTestingController);
    });
    afterEach(() => {
        httpMock.verify();
    });

    describe('#getAdapters', () => {
        it('should create Get to /api/v2/adapter/allrunning', () => {
            service.getAdapters().subscribe(res => res);
            const req = httpMock.expectOne('/streampipes-backend/api/v2/adapter/allrunning');
            expect(req.request.method).toBe('GET');
        });
    });

    describe('#getProtocols', () => {
        it('should create Get to /api/v2/adapter/allProtocols', () => {
            service.getProtocols().subscribe(res => res);
            const req = httpMock.expectOne('/streampipes-backend/api/v2/adapter/allProtocols');
            expect(req.request.method).toBe('GET');
        });
    });

    describe('#getFormats', () => {
        it('should create Get to /api/v2/adapter/allFormats', () => {
            service.getFormats().subscribe(res => res);
            const req = httpMock.expectOne('/streampipes-backend/api/v2/adapter/allFormats');
            expect(req.request.method).toBe('GET');
        });
    });

    describe('#deleteAdapter', () => {
        it('should create Delete to /api/v2/adapter/:adapterId', () => {
            let adapterDescription = new AdapterDescription('test');
            adapterDescription.couchDbId = 'test';
            service.deleteAdapter(adapterDescription).subscribe(res => res);
            const req = httpMock.expectOne('/streampipes-backend/api/v2/adapter/test');
            expect(req.request.method).toBe('DELETE');
        });
    });
});
