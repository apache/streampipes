import { AdapterStreamDescription } from "./AdapterStreamDescription";
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {ProtocolDescription} from './grounding/ProtocolDescription';
import {FormatDescription} from './grounding/FormatDescription';

@RdfsClass('sp:GenericAdapterStreamDescription')
export class GenericAdapterStreamDescription extends AdapterStreamDescription {

    @RdfProperty('sp:hasProtocol')
    public protocol: ProtocolDescription;
  
    @RdfProperty('sp:hasFormat')
    public format: FormatDescription;

    constructor(id: string) {
        super(id);
        this.appId = id;
    }

}