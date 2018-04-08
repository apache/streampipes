import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { DataSetDescription } from '../../connect/model/DataSetDescription';
import { TsonLd } from '../../connect/tsonld/tsonld';
import { ProtocolDescription } from '../../connect/model/ProtocolDescription';
import { ProtocolDescriptionList } from '../../connect/model/ProtocolDescriptionList';
import { FreeTextStaticProperty } from '../../connect/model/FreeTextStaticProperty';
import { FormatDescriptionList } from '../../connect/model/FormatDescriptionList';
import { FormatDescription } from '../../connect/model/FormatDescription';
import { AdapterDescriptionList } from '../../connect/model/AdapterDescriptionList';
import { AdapterDescription } from '../../connect/model/AdapterDescription';
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
import {URI} from '../../connect/model/URI';

@Injectable()
export class KviService {

    constructor(private http: HttpClient) {
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
        tsonld.addClassMapping(DataStreamContainer);
        tsonld.addClassMapping(DataSetDescription);
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
            .get(this.getServerUrl() + '/api/v2/users/zehnder@fzi.de/pipeline-templates/sets')
            .map(response => {
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:DataStreamContainer');
                return res.list;
            });
    }

    getOperators(dataSet: DataSetDescription): Observable<PipelineTemplateDescription[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/zehnder@fzi.de/pipeline-templates?dataset=' + dataSet.id)
            .map(response => {
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:PipelineTemplateDescriptionContainer');
                return res.list;
            });
    }

    getStaticProperties(dataSet: DataSetDescription, operator: PipelineTemplateDescription): Observable<PipelineTemplateInvocation> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/zehnder@fzi.de/pipeline-templates/invocations?streamId=' + dataSet.id + '&templateId=' + operator.internalName)
            .map(response => {
                const tsonld = this.getTsonLd();
                const res = tsonld.fromJsonLdType(response, 'sp:PipelineTemplateInvocation');
                return res;
            });
    }

    createPipelineTemplateInvocation(invocation: PipelineTemplateInvocation) {
        console.log(invocation);
        const tsonld = this.getTsonLd();



       tsonld.toflattenJsonLd(invocation).subscribe(res => {
            this.http
            .post(this.getServerUrl() + '/api/v2/users/zehnder@fzi.de/pipeline-templates', res)
            .subscribe();
        });


        // const res = tsonld.toJsonLd(invocation);
        // return this.http
        //     .post(this.getServerUrl() + '/api/v2/users/zehnder@fzi.de/pipeline-templates', res)
        //     .map(response => {
        //         return response;
        //     });
    }

}