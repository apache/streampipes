import {UserInput} from '../model/UserInput';
import {PipelineElementInput} from '../model/PipelineElementInput';

export class PipelineElementBuilder {

    pipelineElementInput: PipelineElementInput;

    constructor(name: string) {
        this.pipelineElementInput = new PipelineElementInput();
        this.pipelineElementInput.name = name;
        this.pipelineElementInput.config = [];
    }

    public static create(name: string) {
        return new PipelineElementBuilder(name);
    }

    public addInput(type: string, selector: string, value: string) {
        const  userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.pipelineElementInput.config.push(userInput);

        return this;
    }

    build() {
        return this.pipelineElementInput;
    }
}
