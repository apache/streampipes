import {UserInput} from './UserInput';
import {AdapterInput} from './AdapterInput';

export class GenericAdapterInput extends AdapterInput{
    protocolConfiguration: UserInput[];
    format: string;
    formatConfiguration: UserInput[];
}