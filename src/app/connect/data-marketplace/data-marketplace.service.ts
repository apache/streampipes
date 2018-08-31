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
import { FormatDescription } from '../model/connect/grounding/FormatDescription';

@Injectable()
export class DataMarketplaceService {
  private host = '/streampipes-connect/';

  constructor(
    private http: HttpClient,
    private authStatusService: AuthStatusService
  ) {}

  private getTsonLd(): TsonLd {
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
    tsonld.addClassMapping(FormatDescription);

    return tsonld;
  }

  getAdapterDescriptions(): Observable<AdapterDescription[]> {
    return this.http
      .get(
        this.host +
          'api/v1/' +
          this.authStatusService.email +
          '/master/description/adapters'
      )
      .map(response => {
        const res = this.getTsonLd().fromJsonLdType(
          response,
          'sp:AdapterDescriptionList'
        );

        return res.list;
      });
  }

  getAdapters(): Observable<AdapterDescription[]> {
    return this.http
      .get(
        this.host +
          'api/v1/' +
          this.authStatusService.email +
          '/master/adapters'
      )
      .map(response => {
        const res = this.getTsonLd().fromJsonLdType(
          response,
          'sp:AdapterDescriptionList'
        );

        return res.list;
      });
  }

  deleteAdapter(adapter: AdapterDescription): Observable<Object> {
    return this.http.delete(
      this.host +
        'api/v1/' +
        this.authStatusService.email +
        '/master/adapters/' +
        adapter.couchDbId
    );
  }

  getProtocols(): Observable<ProtocolDescription[]> {
    return this.http
      .get(
        this.host +
          'api/v1/' +
          this.authStatusService.email +
          '/master/description/protocols'
      )
      .map(response => {
        const res = this.getTsonLd().fromJsonLdType(
          response,
          'sp:ProtocolDescriptionList'
        );

        return res.list;
      });
  }

  getGenericAndSpecifigAdapterDescriptions(): Observable<
    Observable<AdapterDescription[]>
  > {
    return this.getAdapterDescriptions().map(adapterDescriptions => {
      adapterDescriptions = adapterDescriptions.filter(
        this.isSpecificDescription
      );
      return this.getProtocols().map(protocols => {
        for (let protocol of protocols) {
          let newAdapterDescription: AdapterDescription;
          if (protocol.id.includes('sp:protocol/set')) {
            newAdapterDescription = new GenericAdapterSetDescription(
              'http://streampipes.org/genericadaptersetdescription'
            );
          } else if (protocol.id.includes('sp:protocol/stream')) {
            newAdapterDescription = new GenericAdapterStreamDescription(
              'http://streampipes.org/genericadapterstreamdescription'
            );
          }
          newAdapterDescription.label = protocol.label;
          newAdapterDescription.description = protocol.description;
          newAdapterDescription.uri = newAdapterDescription.id;
          if (
            newAdapterDescription instanceof GenericAdapterSetDescription ||
            newAdapterDescription instanceof GenericAdapterStreamDescription
          ) {
            newAdapterDescription.protocol = protocol;
          }
          adapterDescriptions.push(newAdapterDescription);
        }
        return adapterDescriptions;
      });
    });
  }

  isDataStreamDescription(adapter: AdapterDescription): boolean {
    return adapter.constructor.name.includes('AdapterStreamDescription');
  }

  isDataSetDescription(adapter: AdapterDescription): boolean {
    return adapter.constructor.name.includes('AdapterSetDescription');
  }

  isGenericDescription(adapter: AdapterDescription): boolean {
    return adapter.id.includes('generic');
  }

  isSpecificDescription(adapter: AdapterDescription): boolean {
    return adapter.id.includes('specific');
  }
}
