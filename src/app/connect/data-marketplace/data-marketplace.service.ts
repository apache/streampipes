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
import { DataStreamDescription } from '../model/DataStreamDescription';
import { EventSchema } from '../schema-editor/model/EventSchema';
import { EventPropertyPrimitive } from '../schema-editor/model/EventPropertyPrimitive';
import { ConnectService } from '../connect.service';
import { AnyStaticProperty } from '../model/AnyStaticProperty';
import { Option } from '../model/Option';
import {RenameRuleDescription} from '../model/connect/rules/RenameRuleDescription';
import {DeleteRuleDescription} from '../model/connect/rules/DeleteRuleDescription';
import {AddNestedRuleDescription} from '../model/connect/rules/AddNestedRuleDescription';
import {MoveRuleDescription} from '../model/connect/rules/MoveRuleDesctiption';
import {EventPropertyNested} from '../schema-editor/model/EventPropertyNested';
import {EventPropertyList} from '../schema-editor/model/EventPropertyList';
import {UUID} from 'angular2-uuid';
import {DataSetDescription} from '../model/DataSetDescription';

@Injectable()
export class DataMarketplaceService {
  private host = '/streampipes-connect/';

  constructor(
    private http: HttpClient,
    private authStatusService: AuthStatusService,
    private connectService: ConnectService
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


    tsonld.addClassMapping(AnyStaticProperty);
    tsonld.addClassMapping(Option);

    tsonld.addClassMapping(ProtocolDescriptionList);
    tsonld.addClassMapping(ProtocolDescription);
    tsonld.addClassMapping(FormatDescription);

    tsonld.addClassMapping(DataStreamDescription);
    tsonld.addClassMapping(DataSetDescription);
    tsonld.addClassMapping(EventSchema);
    tsonld.addClassMapping(EventPropertyPrimitive);
    tsonld.addClassMapping(EventPropertyNested);
    tsonld.addClassMapping(EventPropertyList);

    tsonld.addClassMapping(FreeTextStaticProperty);

    tsonld.addClassMapping(RenameRuleDescription);
    tsonld.addClassMapping(DeleteRuleDescription);
    tsonld.addClassMapping(AddNestedRuleDescription);
    tsonld.addClassMapping(MoveRuleDescription);

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
        if(response['@graph'] === undefined) return [];
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
        this.connectService.isSpecificDescription
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
}
