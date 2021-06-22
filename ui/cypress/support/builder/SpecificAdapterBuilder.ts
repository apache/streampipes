import {UserInput} from '../model/UserInput';
import {SpecificAdapterInput} from '../model/SpecificAdapterInput';

export class SpecificAdapterBuilder {


    specificAdapterInput: SpecificAdapterInput;

    constructor(type: string) {
        this.specificAdapterInput = new SpecificAdapterInput();
        this.specificAdapterInput.adapterType = type;
        this.specificAdapterInput.adapterConfiguration = [];
    }

    public static create(name: string) {
        return new SpecificAdapterBuilder(name);
    }

    public setName(name: string) {
        this.specificAdapterInput.adapterName = name;
        return this;
    }

    public setTimestampProperty(timestsmpProperty: string) {
        this.specificAdapterInput.timestampProperty = timestsmpProperty;
        return this;
    }

    public addInput(type: string, selector: string, value: string) {
        const  userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.specificAdapterInput.adapterConfiguration.push(userInput);

        return this;
    }

    build() {
        return this.specificAdapterInput;
    }
}