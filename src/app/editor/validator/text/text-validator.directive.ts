
export class TextValidatorDirective {

    fieldCtrl: any;
    textValidator: any;
    staticProperty: any;
    restrict: any;
    require: any;

    primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
        {"id": "http://www.w3.org/2001/XMLSchema#boolean"},
        {"id": "http://www.w3.org/2001/XMLSchema#integer"},
        {"id": "http://www.w3.org/2001/XMLSchema#long"},
        {"id": "http://www.w3.org/2001/XMLSchema#double"}];

    constructor() {
        this.restrict = 'A';
        this.require = 'ngModel';
    }

    link(scope, elm, attrs, ctrl) {
        this.fieldCtrl = ctrl;
        scope.$watch(attrs.textValidator, newVal => {
            this.staticProperty = newVal;
            this.addValidator();
        });
    }

    addValidator() {
        this.fieldCtrl.$validators.textValidator = (modelValue, viewValue) => this.validateText(modelValue, viewValue);
    }

    validateText(modelValue, viewValue) {
        console.log("validating text");
        if (this.staticProperty.properties.requiredDatatype) {
            return this.typeCheck(modelValue, this.staticProperty.properties.requiredDatatype);
        } else if (this.staticProperty.properties.requiredDomainProperty) {
            // TODO why is type info stored in required domain property??
            return this.typeCheck(modelValue, this.staticProperty.properties.requiredDomainProperty);
        } else {
            return true;
        }

    }

    typeCheck(property, datatype) {
        if (datatype == this.primitiveClasses[0].id) return true;
        if (datatype == this.primitiveClasses[1].id) return (property == 'true' || property == 'false');
        if (datatype == this.primitiveClasses[2].id) return (!isNaN(property) && parseInt(Number(property)+'') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[3].id) return (!isNaN(property) && parseInt(Number(property)+'') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[4].id) return !isNaN(property);
        return false;
    }
}