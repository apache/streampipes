import { AddDialogController } from './add-dialog.controller';
import { DialogController } from './dialog.controller';
import { ContextController } from './context.controller';


// export default function OntologyCtrl($scope, RestApi, $mdToast, $mdDialog) {
export class OntologyCtrl {

    constructor(RestApi, $mdToast, $mdDialog) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$mdDialog = $mdDialog;

        this.primitiveClasses = [{
            "title": "String",
            "description": "A textual datatype, e.g., 'machine1'",
            "id": "http://www.w3.org/2001/XMLSchema#string"
        },
            {"title": "Boolean", "description": "A true/false value", "id": "http://www.w3.org/2001/XMLSchema#boolean"},
            {
                "title": "Integer",
                "description": "A whole-numerical datatype, e.g., '1'",
                "id": "http://www.w3.org/2001/XMLSchema#integer"
            },
            {
                "title": "Double",
                "description": "A floating-point number, e.g., '1.25'",
                "id": "http://www.w3.org/2001/XMLSchema#double"
            }];

        this.rangeTypes = [{
            "title": "Primitive Type",
            "description": "A primitive type, e.g., a number or a textual value",
            "rangeType": "PRIMITIVE"
        },
            {
                "title": "Enumeration",
                "description": "A textual value with a specified value set",
                "rangeType": "ENUMERATION"
            },
            {
                "title": "Quantitative Value",
                "description": "A numerical value within a specified range",
                "rangeType": "QUANTITATIVE_VALUE"
            }];


        this.properties = [];
        this.propertySelected = false;
        this.propertyDetail = {};
        this.selectedPrimitive = this.primitiveClasses[0];
        this.selectedRangeType = this.rangeTypes[0].rangeType;

        this.concepts = [];
        this.conceptSelected = false;
        this.conceptDetail = {};
        this.currentlySelectedClassProperty;

        this.instanceSelected = false;
        this.instanceDetail = {};
        this.selectedInstanceProperty = "";

        this.selectedTab = "CONCEPTS";

        this.loadProperties();
        this.loadConcepts();

    }

    setSelectedTab(type) {
        this.selectedTab = type;
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .success(propertiesData => {
                this.properties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    loadConcepts() {
        this.RestApi.getOntologyConcepts()
            .success(conceptsData => {
                this.concepts = conceptsData;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    loadPropertyDetails(propertyId) {
        this.RestApi.getOntologyPropertyDetails(propertyId)
            .success(propertiesData => {
                this.propertyDetail = propertiesData;
                this.propertySelected = true;
            })
            .error(msg => {
                this.propertySelected = false;
                console.log(msg);
            });
    };

    loadConceptDetails(conceptId) {
        this.instanceSelected = false;
        this.RestApi.getOntologyConceptDetails(conceptId)
            .success(conceptData => {
                this.conceptDetail = conceptData;
                this.conceptSelected = true;
            })
            .error(msg => {
                this.conceptSelected = false;
                console.log(msg);
            });
    };

    loadInstanceDetails(instanceId) {
        this.RestApi.getOntologyInstanceDetails(instanceId)
            .success(instanceData => {
                this.instanceDetail = instanceData;
                this.instanceSelected = true;
            })
            .error(msg => {
                this.instanceSelected = false;
                console.log(msg);
            });
    };

    addPropertyToClass(p) {
        if (!this.conceptDetail.domainProperties) this.conceptDetail.domainProperties = [];
        this.RestApi.getOntologyPropertyDetails(p)
            .success(propertiesData => {
                this.conceptDetail.domainProperties.push(propertiesData);
            })
            .error(msg => {
                console.log(msg);
            });
    }

    addPropertyToInstance() {
        if (!this.instanceDetail.domainProperties) this.instanceDetail.domainProperties = [];
        this.RestApi.getOntologyPropertyDetails(this.selectedInstanceProperty)
            .success(propertiesData => {
                this.instanceDetail.domainProperties.push(propertiesData);
            })
            .error(msg => {
                console.log(msg);
            });

    }

    removePropertyFromInstancefunction(property) {
        this.instanceDetail.domainProperties.splice(this.instanceDetail.domainProperties.indexOf(property), 1);
    }

    removePropertyFromClass(property) {
        this.conceptDetail.domainProperties.splice(this.conceptDetail.domainProperties.indexOf(property), 1);
    }

    storeClass() {
        this.loading = true;
        this.RestApi.updateOntologyConcept(this.conceptDetail.elementHeader.id, this.conceptDetail)
            .success(msg => {
                this.loading = false;
                this.showToast("Concept updated.");
            })
            .error(msg => {
                this.loading = false;
            });
    }

    storeInstance() {
        this.loading = true;
        this.RestApi.updateOntologyInstance(this.instanceDetail.elementHeader.id, this.instanceDetail)
            .success(msg => {
                this.loading = false;
                this.showToast("Instance updated.");
                this.loadConcepts();
            })
            .error(msg => {
                this.loading = false;
            });
    }

    addTypeDefinition() {
        this.propertyDetail.range = {};
        this.propertyDetail.range.rangeType = this.selectedRangeType;
        this.propertyDetail.rangeDefined = true;
        if (this.selectedRangeType === 'PRIMITIVE')
        {
            this.propertyDetail.rdfsDatatype = "";
        } else if (this.selectedRangeType === 'QUANTITATIVE_VALUE')
        {
            this.propertyDetail.range.minValue = -1;
            this.propertyDetail.range.maxValue = -1;
            this.propertyDetail.range.unitCode = "";
        } else if (this.selectedRangeType === 'ENUMERATION')
        {
            this.propertyDetail.range.minValue = -1;
            this.propertyDetail.range.maxValue = -1;
            this.propertyDetail.range.unitCode = "";
        }
    }

    reset() {
        this.propertyDetail.rangeDefined = false;
    }

    updateProperty() {
        this.loading = true;
        this.propertyDetail.labelDefined = true;
        this.RestApi.updateOntologyProperty(this.propertyDetail.elementHeader.id, this.propertyDetail)
            .success(msg => {
                this.loading = false;
                this.showToast("Property updated.");
            })
            .error(msg => {
                this.loading = false;

            });
    }

    openNamespaceDialog(){
        this.$mdDialog.show({
            controller: DialogController,
            controllerAs: 'ctrl',
            templateUrl: 'app/ontology/templates/manageNamespacesDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose:true,
        })
    };

    openAddElementDialog(elementType){
        this.openAddElementDialog(elementType, undefined);
    };


    deleteConcept(conceptId) {
        this.RestApi.deleteOntologyConcept(conceptId)
            .success(msg => {
                this.loadConcepts();
                this.conceptSelected = false;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    deleteProperty(propertyId) {
        this.RestApi.deleteOntologyProperty(propertyId)
            .success(msg => {
                this.loadProperties();
                this.propertySelected = false;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    deleteInstance(instanceId) {
        this.RestApi.deleteOntologyInstance(instanceId)
            .success(msg => {
                this.loadConcepts();
                this.instanceSelected = false;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    // TODO fix after refactoring
    // $scope.$on('loadProperty', function(event, propertyId) {
    //     $scope.loadPropertyDetails(propertyId);
    // });



    showToast(text) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .content(text)
                .position("top right")
                .hideDelay(3000)
        );
    }

    openImportDialog() {
        this.$mdDialog.show({
            controller: ContextController,
            controllerAs: 'ctrl',
            templateUrl: 'app/ontology/templates/manageVocabulariesDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose:true,
        })
    }

    openAddElementDialog(elementType, conceptId){
        this.$mdDialog.show({
            controller: AddDialogController,
            controllerAs: 'ctrl',
            templateUrl: 'app/ontology/templates/createElementDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose:true,
            // scope: this,
            locals : {
                elementType : elementType,
                conceptId : conceptId,
                loadConcepts: this.loadConcepts,
                loadConceptDetails: this.loadConceptDetails,
                loadProperties: this.loadProperties,
                loadPropertyDetails: this.loadPropertyDetails,
                loadInstanceDetails: this.loadInstanceDetails

            }
        })
    }

};

OntologyCtrl.$inject = ['RestApi', '$mdToast', '$mdDialog'];
