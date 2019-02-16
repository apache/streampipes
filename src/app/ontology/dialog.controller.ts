export class DialogController {

	RestApi: any;
	$mdDialog: any;
	namespaces: any;
	addSelected: any;
	newNamespace: any;

    constructor($mdDialog, RestApi) {
    	this.RestApi = RestApi;
    	this.$mdDialog = $mdDialog;
    	this.namespaces = [];
		this.addSelected = false;
		this.newNamespace = {};
	}

	$onInit() {
        this.getNamespaces();
	}

	getNamespaces() {
		this.RestApi.getOntologyNamespaces()
			.then(namespaces => {
				this.namespaces = namespaces.data;
			});
	}

	addNamespace() {
		this.RestApi.addOntologyNamespace(this.newNamespace)
			.then(msg => {
				this.addSelected = false;
				this.newNamespace = {};
				this.getNamespaces();
			}, msg => {
				this.addSelected = false;
				console.log(msg);
			}); 		
	}

	deleteNamespace(prefix) {
		this.RestApi.deleteOntologyNamespace(prefix)
			.then(msg => {
				this.getNamespaces();
			});
	}

	showAddInput() {
		this.addSelected = true;
		this.newNamespace.prefix = "";
		this.newNamespace.name = "";
	}

	hide() {
		this.$mdDialog.hide();
	};

	cancel() {
		this.$mdDialog.cancel();
	};

}

DialogController.$inject = ['$mdDialog', 'RestApi'];
