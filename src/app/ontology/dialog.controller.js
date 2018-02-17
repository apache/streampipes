export class DialogController {

    constructor($mdDialog, RestApi) {
    	this.RestApi = RestApi;
    	this.$mdDialog = $mdDialog;
    	this.namespaces = [];
		this.addSelected = false;
		this.newNamespace = {};

        this.getNamespaces();
	}

	getNamespaces() {
		this.RestApi.getOntologyNamespaces()
			.success(namespaces => {
				this.namespaces = namespaces;
			})
			.error(function(msg){
				console.log(msg);
			});
	}

	addNamespace() {
		this.RestApi.addOntologyNamespace(this.newNamespace)
			.success(msg => {
				this.addSelected = false;
				this.newNamespace = {};
				this.getNamespaces();
			})
			.error(msg => {
				this.addSelected = false;
				console.log(msg);
			}); 		
	}

	deleteNamespace(prefix) {
		this.RestApi.deleteOntologyNamespace(prefix)
			.success(msg => {
				this.getNamespaces();
			})
			.error(msg => {
				console.log(msg);
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
