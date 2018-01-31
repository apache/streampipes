import {JsonLdDialogController} from "./dialog/jsonldDialog.controller";

export class MyElementsCtrl {

    constructor(restApi, $mdToast, $mdDialog) {
        this.restApi = restApi;
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
            return element.uri;
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
        this.restApi.getOwnActions()
            .success(actions => {
                this.currentElements = actions;
            })
            .error(error => {
                this.status = 'Unable to load actions: ' + error.message;
            });
    }

    loadOwnSepas() {
        this.restApi.getOwnSepas()
            .success(sepas => {
                this.currentElements = sepas;
            })
            .error(error => {
                this.status = 'Unable to load sepas: ' + error.message;
            });
    }

    loadOwnSources() {
        this.restApi.getOwnSources()
            .success(sources => {
                this.currentElements = sources;
            })
            .error(error => {
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
        this.restApi.update(elementUri)
            .success(msg => {
                this.showToast(msg.notifications[0].title);
            })
            .then(() => {
                this.loadCurrentElements(type);
            });
    }

    remove(elementUri, type) {
        this.restApi.del(elementUri)
            .success(msg => {
                this.showToast(msg.notifications[0].title);
                this.loadCurrentElements(type);
            });
    }

    jsonld(event, elementUri) {
        this.restApi.jsonld(elementUri)
            .success(msg => {
                this.showAlert(event, elementUri, msg);
            });
    }

    toggleFavoriteAction(action, type) {
        if (action.favorite) {
            this.restApi.removePreferredAction(action.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.data.name);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.restApi.addPreferredAction(action.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
    }

    toggleFavoriteSepa(sepa, type) {
        if (sepa.favorite) {
            this.restApi.removePreferredSepa(sepa.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.restApi.addPreferredSepa(sepa.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
    }

    toggleFavoriteSource(source, type) {
        if (source.favorite) {
            this.restApi.removePreferredSource(source.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.notifications[0].title);
                })
                .then(() => {
                    this.loadCurrentElements(type);
                });
        }
        else {
            this.restApi.addPreferredSource(source.elementId)
                .success(msg => {
                    this.showToast(msg.notifications[0].title);
                })
                .error(error => {
                    this.showToast(error.notifications[0].title);
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
            controllerAs: '$ctrl',
            templateUrl: 'app/myelements/dialog/jsonldDialog.tmpl.html',
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

MyElementsCtrl.$inject = ['restApi', '$mdToast', '$mdDialog'];