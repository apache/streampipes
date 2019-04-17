import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { DataSetDescription } from '../../connect/model/DataSetDescription';
import { TsonLd } from '../../platform-services/tsonld/tsonld';
import { ProtocolDescription } from '../../connect/model/connect/grounding/ProtocolDescription';
import { ProtocolDescriptionList } from '../../connect/model/connect/grounding/ProtocolDescriptionList';
import { FreeTextStaticProperty } from '../../connect/model/FreeTextStaticProperty';
import { FormatDescriptionList } from '../../connect/model/connect/grounding/FormatDescriptionList';
import { FormatDescription } from '../../connect/model/connect/grounding/FormatDescription';
import { AdapterDescriptionList } from '../../connect/model/connect/AdapterDescriptionList';
import { AdapterDescription } from '../../connect/model/connect/AdapterDescription';
import { EventSchema } from '../../connect/schema-editor/model/EventSchema';
import { EventPropertyNested } from '../../connect/schema-editor/model/EventPropertyNested';
import { EventPropertyList } from '../../connect/schema-editor/model/EventPropertyList';
import { EventPropertyPrimitive } from '../../connect/schema-editor/model/EventPropertyPrimitive';
import { DomainPropertyProbability } from '../../connect/schema-editor/model/DomainPropertyProbability';
import { DomainPropertyProbabilityList } from '../../connect/schema-editor/model/DomainPropertyProbabilityList';
import { GuessSchema } from '../../connect/schema-editor/model/GuessSchema';
import { EventProperty } from '../../connect/schema-editor/model/EventProperty';
import { DataStreamContainer } from '../../connect/model/DataStreamContainer';
import { PipelineTemplateInvocation } from '../../connect/model/PipelineTemplateInvocation';
import { PipelineTemplateDescription } from '../../connect/model/PipelineTemplateDescription';
import { PipelineTemplateDescriptionContainer } from '../../connect/model/PipelineTemplateDescriptionContainer';
import { StaticProperty } from '../../connect/model/StaticProperty';
import { MappingPropertyUnary } from '../../connect/model/MappingPropertyUnary';
import { URI } from '../../connect/model/URI';
import { AuthStatusService } from '../../services/auth-status.service';
import { DataStreamDescription } from '../../connect/model/DataStreamDescription';
import { Enumeration } from '../../connect/schema-editor/model/Enumeration';
import { QuantitativeValue } from '../../connect/schema-editor/model/QuantitativeValue';
import { BoundPipelineElement } from '../../connect/model/BoundPipelineElement';
import { DataSinkInvocation } from '../../connect/model/DataSinkInvocation';
import 'rxjs-compat/add/operator/map';
import { PipelineTemplateService } from '../../platform-services/apis/pipeline-template.service';
import {TsonLdSerializerService} from '../../platform-services/tsonld-serializer.service';

@Injectable()
export class KviService {

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService,
        private pipelineTemplateService: PipelineTemplateService,
        private tsonLdSerializerService: TsonLdSerializerService) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getDataSets(): Observable<DataSetDescription[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates/streams')
            .pipe(map(response => {



                // TODO remove this
                // quick fix to deserialize URIs
                response['@graph'].forEach(function (object) {
                   if (object['sp:domainProperty'] != undefined) {
                       // object['sp:domainProperty']['@type'] = "sp:URI";
                       object['sp:domainProperty'] = object['sp:domainProperty']['@id'];
                       delete object['sp:domainProperty']['@id'];
                   }
                });

                const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:DataStreamContainer');
                return res.list;
            }));
    }

    getOperators(dataSet: DataSetDescription): Observable<PipelineTemplateDescription[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates?dataset=' + dataSet.id)
            .pipe(map(response => {
                const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:PipelineTemplateDescriptionContainer');
                return res.list;
            }));
    }

    getStaticProperties(dataSet: DataSetDescription, operator: PipelineTemplateDescription): Observable<PipelineTemplateInvocation> {

        return this.pipelineTemplateService.getPipelineTemplateInvocation(dataSet.id, operator.appId);
    }

    isFreeTextStaticProperty(val) {
        return val instanceof FreeTextStaticProperty;
    }

    asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }

    createPipelineTemplateInvocation(invocation: PipelineTemplateInvocation) {
        this.pipelineTemplateService.createPipelineTemplateInvocation(invocation);
    }

}