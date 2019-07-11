import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { DataSetDescription } from '../../connect/model/DataSetDescription';
import { TsonLd } from '../tsonld/tsonld';
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
import {BoundPipelineElement} from '../../connect/model/BoundPipelineElement';
import {DataSinkInvocation} from '../../connect/model/DataSinkInvocation';
import 'rxjs-compat/add/operator/map';
import {TsonLdSerializerService} from '../tsonld-serializer.service';

@Injectable()
export class PipelineTemplateService {

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService,
        private tsonLdSerializerService: TsonLdSerializerService,
    ) {}

    getServerUrl() {
        return '/streampipes-backend';
    }

    // getDataSets(): Observable<DataSetDescription[]> {
    //     return this.http
    //         .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates/streams')
    //         .pipe(map(response => {
    //
    //
    //
    //             // TODO remove this
    //             // quick fix to deserialize URIs
    //             response['@graph'].forEach(function (object) {
    //                if (object['sp:domainProperty'] != undefined) {
    //                    // object['sp:domainProperty']['@type'] = "sp:URI";
    //                    object['sp:domainProperty'] = object['sp:domainProperty']['@id'];
    //                    delete object['sp:domainProperty']['@id'];
    //                }
    //             });
    //
    //             const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:DataStreamContainer');
    //             return res.list;
    //         }));
    // }
    //
    // getOperators(dataSet: DataSetDescription): Observable<PipelineTemplateDescription[]> {
    //     return this.http
    //         .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates?dataset=' + dataSet.id)
    //         .pipe(map(response => {
    //             const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:PipelineTemplateDescriptionContainer');
    //             return res.list;
    //         }));
    // }

    getPipelineTemplateInvocation(dataSetId:string, templateId: string): Observable<PipelineTemplateInvocation> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates/invocation?streamId=' + dataSetId + '&templateId=' + templateId)
            .pipe(map(response => {

                var res: PipelineTemplateInvocation;

                // Currently tsonld dows not support objects that just contain one root object without an enclosing @graph array
                if ('@graph' in response) {
                    res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:PipelineTemplateInvocation');
                } else {
                    res = new PipelineTemplateInvocation(response['@id']);
                    res.dataSetId = response['sp:hasDataSetId'];
                    res.name = response['hasElementName'];
                    res.pipelineTemplateId = response['sp:hasInternalName'];
                }

                // TODO find better solution
                // This will remove preconfigured values from the UI
                res.list.forEach(property => {
                    if (this.isFreeTextStaticProperty(property)) {
                        if (this.asFreeTextStaticProperty(property).value != undefined) {
                            this.asFreeTextStaticProperty(property).render = false;
                        }
                    }
                });
                return res;
            }));
    }

    isFreeTextStaticProperty(val) {
        return val instanceof FreeTextStaticProperty;
    }

    asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }

    createPipelineTemplateInvocation(invocation: PipelineTemplateInvocation) {

        this.tsonLdSerializerService.toJsonLd(invocation).subscribe(res => {
            this.http
                .post(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates', res)
                .subscribe();
        });
    }

}