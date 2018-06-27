import { Injectable } from '@angular/core';
import { StaticProperty } from '..//model/StaticProperty';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
@Injectable()



export class StaticPropertyUtilService{

    public asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }
}