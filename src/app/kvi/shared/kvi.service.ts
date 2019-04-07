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

@Injectable()
export class KviService {

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService,
        private pipelineTemplateService: PipelineTemplateService) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    private getTsonLd(): any {

        const tsonld = new TsonLd();
        tsonld.addClassMapping(ProtocolDescription);
        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(FreeTextStaticProperty);
        tsonld.addClassMapping(MappingPropertyUnary);
        tsonld.addClassMapping(FormatDescriptionList);
        tsonld.addClassMapping(FormatDescription);
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(Enumeration);
        tsonld.addClassMapping(QuantitativeValue);
        tsonld.addClassMapping(DataStreamContainer);
        tsonld.addClassMapping(DataSetDescription);
        tsonld.addClassMapping(BoundPipelineElement);
        tsonld.addClassMapping(DataSinkInvocation);
        tsonld.addClassMapping(DataStreamDescription);
        tsonld.addClassMapping(PipelineTemplateInvocation);
        tsonld.addClassMapping(PipelineTemplateDescription);
        tsonld.addClassMapping(PipelineTemplateDescriptionContainer);
        tsonld.addClassMapping(EventSchema);
        tsonld.addClassMapping(EventProperty);
        tsonld.addClassMapping(EventPropertyNested);
        tsonld.addClassMapping(EventPropertyList);
        tsonld.addClassMapping(EventPropertyPrimitive);
        tsonld.addClassMapping(DomainPropertyProbability);
        tsonld.addClassMapping(DomainPropertyProbabilityList);
        tsonld.addClassMapping(GuessSchema);
        tsonld.addClassMapping(URI);

        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('xsd', 'http://www.w3.org/2001/XMLSchema#');
        tsonld.addContext('empire', 'urn:clarkparsia.com:empire:');


        return tsonld;
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

                const tsonld = this.getTsonLd();

                const res = tsonld.fromJsonLdType(response, 'sp:DataStreamContainer');
                return res.list;
            }));
    }

    getOperators(dataSet: DataSetDescription): Observable<PipelineTemplateDescription[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates?dataset=' + dataSet.id)
            .pipe(map(response => {
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:PipelineTemplateDescriptionContainer');
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