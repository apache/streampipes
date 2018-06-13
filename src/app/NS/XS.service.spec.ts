import { TestBed, getTestBed } from "@angular/core/testing";
import { xsService } from "./XS.service";

describe('XSService', () =>{

        let injector: TestBed;
        let service: xsService;
    beforeEach(() => {
        

        TestBed.configureTestingModule({
            imports: [

            ],
            providers: [
                xsService
            ]
        });
        injector = getTestBed();
        service = injector.get(xsService)

    });

    it("should get string XS String", () => {
        expect(service.XS_STRING).toBe("xs:string")
    });

    it("should get string XS INTEGER", () => {
        expect(service.XS_INTEGER).toBe( "xs:integer")
    });

    it("should get string XS DOUBLE", () => {
        expect(service.XS_DOUBLE).toBe("xs:double")
    });

    it("should get string XS BOOLEAN", () => {
        expect(service.XS_BOOLEAN).toBe("xs:boolean")
    });

});