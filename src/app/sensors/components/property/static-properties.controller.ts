import * as angular from 'angular';

export class StaticPropertiesController {

    RestApi: any;
    staticPropertyTypes: any;
    newStaticPropertyType: any;
    memberTypeSelected: any;
    properties: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.staticPropertyTypes = [{
            label: "Text Input",
            "type": "org.streampipes.model.staticproperty.FreeTextStaticProperty"
        },
            {label: "Single-Value Selection", "type": "org.streampipes.model.staticproperty.OneOfStaticProperty"},
            {label: "Multi-Value Selection", "type": "org.streampipes.model.staticproperty.AnyStaticProperty"},
            {label: "Domain Concept", "type": "org.streampipes.model.staticproperty.DomainStaticProperty"},
            {
                label: "Single-Value Mapping Property",
                "type": "org.streampipes.model.staticproperty.MappingPropertyUnary"
            },
            {label: "Multi-Value Mapping Property", "type": "org.streampipes.model.staticproperty.MappingPropertyNary"},
            {label: "Collection", "type": "org.streampipes.model.staticproperty.CollectionStaticProperty"}];

        this.newStaticPropertyType = this.staticPropertyTypes[0].type;
        this.memberTypeSelected = false;
        this.properties = [];

        this.loadProperties();
    }

    range(count) {
        return new Array(+count);
    }

    isSelectedProperty(mapsFrom, property) {
        return (property.properties.elementName == mapsFrom);
    };

    addStaticProperty(staticProperties, type) {
        if (staticProperties == undefined) staticProperties = [];
        staticProperties.push(this.getNewStaticProperty(type));
    }

    getNewStaticProperty(type) {
        if (type === this.staticPropertyTypes[0].type)
            return {"type": this.staticPropertyTypes[0].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[1].type)
            return {
                "type": this.staticPropertyTypes[1].type,
                "properties": {"label": "", "description": "", "options": []}
            };
        else if (type === this.staticPropertyTypes[2].type)
            return {
                "type": this.staticPropertyTypes[2].type,
                "properties": {"label": "", "description": "", "options": []}
            };
        else if (type === this.staticPropertyTypes[3].type)
            return {
                "type": this.staticPropertyTypes[3].type,
                "properties": {"label": "", "description": "", "supportedProperties": []}
            };
        else if (type === this.staticPropertyTypes[4].type)
            return {"type": this.staticPropertyTypes[4].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[5].type)
            return {"type": this.staticPropertyTypes[5].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[6].type)
            return {
                "type": this.staticPropertyTypes[6].type,
                "properties": {"label": "", "description": "", "memberType": "", "members": []}
            };
    }

    getType(property) {
        var label;
        angular.forEach(this.staticPropertyTypes, function (value) {
            if (value.type == property.type) label = value.label;
        });
        return label;
    };

    domainPropertyRestricted(property) {
        return (property.type != undefined);
    };

    toggleDomainPropertyRestriction(property) {
        if (property.type != undefined) property.type = undefined;
        else property.type = this.properties[0].id;
    }

    addMember(property) {
        property.members.push(angular.copy(this.getNewStaticProperty(property.memberType)));
        this.memberTypeSelected = true;
    }

    removeMember(property) {
        property.members = [];
        property.memberType = '';
        this.memberTypeSelected = false;
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .success(propertiesData =>  {
                this.properties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
            });
    }
}

StaticPropertiesController.$inject = ['RestApi'];