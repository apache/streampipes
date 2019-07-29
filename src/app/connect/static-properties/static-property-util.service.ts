import { Injectable } from '@angular/core';
import { StaticProperty } from '../model/StaticProperty';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import {SecretStaticProperty} from "../model/SecretStaticProperty";
@Injectable()



export class StaticPropertyUtilService{

    public asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }

    public asSecretStaticProperty(val: StaticProperty): SecretStaticProperty {
        return <SecretStaticProperty> val;
    }
}