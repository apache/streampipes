import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import { AdapterSetDescription } from './AdapterSetDescription';
import {FormatDescription} from './grounding/FormatDescription';
import {ProtocolDescription} from './grounding/ProtocolDescription';

@RdfsClass('sp:GenericAdapterSetDescription')
export class GenericAdapterSetDescription extends AdapterSetDescription {

    @RdfProperty('sp:hasProtocol')
    public protocol: ProtocolDescription;
  
    @RdfProperty('sp:hasFormat')
    public format: FormatDescription;

    constructor(id: string) {
        super(id)
    }

}