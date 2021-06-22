export class UserInput {
    type: string | 'checkbox' | 'input' | 'file' | 'drop-down' | 'radio';
    selector: string;
    value: string;
}
