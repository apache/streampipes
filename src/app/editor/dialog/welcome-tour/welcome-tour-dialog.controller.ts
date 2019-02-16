export class WelcomeTourDialogController {

    $mdDialog: any;
    ShepherdService: any;
    user: any;
    RestApi: any;

    constructor($mdDialog, RestApi, ShepherdService, user) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.ShepherdService = ShepherdService;
        this.user = user;
    }

    startCreatePipelineTour() {
        this.$mdDialog.hide();
        this.ShepherdService.startCreatePipelineTour();
    }

    hideTourForever() {
        this.user.hideTutorial = true;
        this.RestApi.updateUserDetails(this.user).then(data => {
            this.$mdDialog.hide();
        });
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

WelcomeTourDialogController.$inject = ['$mdDialog', 'RestApi', 'ShepherdService', 'user'];