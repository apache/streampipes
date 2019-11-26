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