import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {OneOfStaticProperty} from "./OneOfStaticProperty";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";

@RdfsClass('sp:RuntimeResolvableOneOfStaticProperty')
export class RuntimeResolvableOneOfStaticProperty extends OneOfStaticProperty {

    @RdfProperty('sp:dependsOnStaticProperty')
    public dependsOn: string[] = [];

    constructor(id: string) {
        super(id);
    }


}