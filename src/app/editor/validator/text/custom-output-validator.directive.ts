export class CustomOutputValidatorDirective {

    staticProperty: any;
    restrict: any;
    require: any;

    constructor() {
        this.restrict = 'A';
        this.require = 'ngModel';
    }

    link(scope, elm, attrs, ctrl) {
        var validator = (value) => {
            ctrl.$setValidity('customOutputValidator', value.length > 0);
            return value;
        };
        ctrl.$parsers.unshift(validator);
        ctrl.$formatters.unshift(validator);
    }
}