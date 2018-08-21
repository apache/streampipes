import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/fromPromise';
import { TsonLd } from '../tsonld';
import { AuthStatusService } from '../../services/auth-status.service';
import { AdapterDescriptionList } from '../model/connect/AdapterDescriptionList';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { AdapterSetDescription } from '../model/connect/AdapterSetDescription';
import { GenericAdapterSetDescription } from '../model/connect/GenericAdapterSetDescription';
import { SpecificAdapterSetDescription } from '../model/connect/SpecificAdapterSetDescription';
import { AdapterStreamDescription } from '../model/connect/AdapterStreamDescription';
import { GenericAdapterStreamDescription } from '../model/connect/GenericAdapterStreamDescription';
import { SpecificAdapterStreamDescription } from '../model/connect/SpecificAdapterStreamDescription';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import { ProtocolDescription } from '../model/connect/grounding/ProtocolDescription';
import { ProtocolDescriptionList } from '../model/connect/grounding/ProtocolDescriptionList';

@Injectable()
export class DataMarketplaceService {

    private host = '/streampipes-connect/';

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    private getTsonLd(): any {

        const tsonld = new TsonLd();
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(AdapterSetDescription);
        tsonld.addClassMapping(GenericAdapterSetDescription);
        tsonld.addClassMapping(SpecificAdapterSetDescription);
        tsonld.addClassMapping(AdapterStreamDescription);
        tsonld.addClassMapping(GenericAdapterStreamDescription);
        tsonld.addClassMapping(SpecificAdapterStreamDescription);

        tsonld.addClassMapping(FreeTextStaticProperty);

        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(ProtocolDescription);

        return tsonld;
    }

    getAdapters(): Observable<AdapterDescription[]> {
        return this.http
            .get(this.host + 'api/v1/' + this.authStatusService.email + '/master/description/adapters')
            .map(response => {
                response['@graph'].forEach(function (object) {
                    if (object['sp:domainProperty'] != undefined) {
                        object['sp:domainProperty'] = object['sp:domainProperty']['@id'];
                        delete object['sp:domainProperty']['@id'];
                    }
                })
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:AdapterDescriptionList');

                return res.list;
            });
    }

    getProtocols(): Observable<ProtocolDescription[]> {
        return this.http
            .get(this.host + 'api/v1/' + this.authStatusService.email + '/master/description/protocols')
            .map(response => {
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:ProtocolDescriptionList');

                return res.list;
            });
    }

}