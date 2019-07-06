import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {StaticProperty} from "../../StaticProperty";
import {RuntimeOptionsRequest} from "./RuntimeOptionsRequest";
import {Option} from "../../Option";

@RdfsClass('sp:RuntimeOptionsResponse')
export class RuntimeOptionsResponse extends RuntimeOptionsRequest {


    @RdfProperty('sp:hasRequestId')
    public requestId: string;

    @RdfProperty('sp:hasOption')
    public options: Option[] = [];


    constructor() {
        super();
        //this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        //this.runtimeKey = runtimeKey;
    }

}
