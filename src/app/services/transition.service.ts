export class TransitionService {

    AuthService: any;
    $mdDialog: any;
    isPipelineAssemblyEmpty: boolean = true;

    constructor(AuthService, $mdDialog) {
        this.AuthService = AuthService;
        this.$mdDialog = $mdDialog;
    }

    onTransitionStarted(transitionInfo) {
        this.AuthService.authenticate();
        if (transitionInfo.$from().name === 'streampipes.editor' && !(this.isPipelineAssemblyEmpty)) {
            return this.showDiscardPipelineDialog().then(() => {
                this.isPipelineAssemblyEmpty = true;
                return true;

            }, function () {
                return false;
            });
        }
    }

    makePipelineAssemblyEmpty(status) {
        this.isPipelineAssemblyEmpty = status;
    }

    getPipelineAssemblyEmpty() {
        return this.isPipelineAssemblyEmpty;
    }

    showDiscardPipelineDialog() {
        var confirm = this.$mdDialog.confirm()
            .title('Discard changes?')
            .textContent('Your current pipeline will be discarded.')
            .ok('Discard Changes')
            .cancel('Cancel');
        return this.$mdDialog.show(confirm);
    }
}