import * as angular from 'angular';

export class AnyController {

    staticProperty: any;

    constructor() {
        console.log(this.staticProperty);
    }
}