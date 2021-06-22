import {UserInput} from '../model/UserInput';
import {SpecificAdapterInput} from '../model/SpecificAdapterInput';
import {GenericAdapterInput} from '../model/GenericAdapterInput';

export class GenericAdapterBuilder {
    genericAdapterInput: GenericAdapterInput;

    constructor(type: string) {
        this.genericAdapterInput = new GenericAdapterInput();
        this.genericAdapterInput.adapterType = type;
        this.genericAdapterInput.protocolConfiguration = [];
        this.genericAdapterInput.formatConfiguration = [];
    }

    public static create(name: string) {
        return new GenericAdapterBuilder(name);
    }

    public setName(name: string) {
        this.genericAdapterInput.adapterName = name;
        return this;
    }

    public setTimestampProperty(timestsmpProperty: string) {
        this.genericAdapterInput.timestampProperty = timestsmpProperty;
        return this;
    }

    public addProtocolInput(type: string, selector: string, value: string) {
        const  userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.genericAdapterInput.protocolConfiguration.push(userInput);

        return this;
    }

    public setFormat(format: string) {
       this.genericAdapterInput.format = format;
       return this;
    }

    public addFormatInput(type: string, selector: string, value: string) {
        const  userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.genericAdapterInput.formatConfiguration.push(userInput);

        return this;
    }

    build() {
        return this.genericAdapterInput;
    }
}