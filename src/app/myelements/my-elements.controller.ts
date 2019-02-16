import * as angular from 'angular';

import {JsonLdDialogController} from "./dialog/jsonldDialog.controller";

export class MyElementsCtrl {

    RestApi: any;
    $mdToast: any;
    $mdDialog: any;
    currentElements: any;
    tabs: any;
    currentTabType: any;
    status: any;

    constructor(RestApi, $mdToast, $mdDialog) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$mdDialog = $mdDialog;
        this.currentElements = [];
        this.tabs = [
            {
                title: 'Data Sources',
                type: 'source'
            },
            {
                title: 'Data Processors',
                type: 'sepa'
            },
            {
                title: 'Data Sinks',
                type: 'action'
            }
        ];
        this.currentTabType = this.tabs[0].type;
    }

    getElementId(element) {
        if (this.currentTabType === 'source') {
            return element.elementId;
        } else {
            return element.belongsTo;
        }
    }

    loadCurrentElements(type) {
        if (type === 'source') {
            this.loadOwnSources();
        }
        else if (type === 'sepa') {
            this.loadOwnSepas();
        }
        else if (type === 'action') {
            this.loadOwnActions();
        }
        this.currentTabType = type;
    }

    loadOwnActions() {
        this.RestApi.getOwnActions()
            .then(actions => {
                this.currentElements = actions.data;
            }, error => {
                this.status = 'Unable to load actions: ' + error.message;
            });
    }

    loadOwnSepas() {
        this.RestApi.getOwnSepas()
            .then(sepas => {
                this.currentElements = sepas.data;
            }, error => {
                this.status = 'Unable to load sepas: ' + error.message;
            });
    }

    loadOwnSources() {
        this.RestApi.getOwnSources()
            .then(sources => {
                this.currentElements = sources.data;
            }, error => {
                this.status = 'Unable to load sepas: ' + error.message;
            });
    }

    elementTextIcon(string) {
        let result = "";
        if (string.length <= 4) {
            result = string;
        } else {
            let words = string.split(" ");
            words.forEach((word, i) => {
                result += word.charAt(0);
            });
        }
        return result.toUpperCase();
    }

    toggleFavorite(element, type) {
        if (type === 'action') this.toggleFavoriteAction(element, type);
        else if (type === 'source') this.toggleFavoriteSource(element, type);
        else if (type === 'sepa') this.toggleFavoriteSepa(element, type);
    }

    refresh(elementUri, type) {
        this.RestApi.update(elementUri)
            .then(msg => {
                this.showToast(msg.data.notifications[0].title);
            })
            .then(() => {
                this.loadCurrentElements(type);
            });
    }

    remove(elementUri, type) {
        this.RestApi.del(elementUri)
            .then(msg => {
                this.showToast(msg.data.notifications[0].title);
                this.loadCurrentElements(type);
            });
    }

    jsonld(event, elementUri) {
        this.RestApi.jsonld(elementUri)
            .then(msg => {
                this.showAlert(event, elementUri, msg.data);
            });
    }

    toggleFavoriteAction(action, type) {
        if (action.favorite) {
            this.RestApi.removePreferredAction(action.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.data.name);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.RestApi.addPreferredAction(action.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
    }

    toggleFavoriteSepa(sepa, type) {
        if (sepa.favorite) {
            this.RestApi.removePreferredSepa(sepa.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.data.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.RestApi.addPreferredSepa(sepa.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.data.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
    }

    toggleFavoriteSource(source, type) {
        if (source.favorite) {
            this.RestApi.removePreferredSource(source.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.data.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.RestApi.addPreferredSource(source.elementId)
                .then(msg => {
                    this.showToast(msg.data.notifications[0].title);
                }, error => {
                    this.showToast(error.data.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
    }

    showToast(string) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .content(string)
                .position("right")
                .hideDelay(3000)
        );
    }

    showAlert(ev, title, content) {
        this.$mdDialog.show({
            controller: JsonLdDialogController,
            controllerAs: 'ctrl',
            templateUrl: 'dialog/jsonldDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                content: content,
                title: title
            },
            bindToController: true
        });
    }

}

MyElementsCtrl.$inject = ['RestApi', '$mdToast', '$mdDialog'];