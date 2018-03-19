export class RegisterCtrl {

    RestApi: any;
    loading: any;
    registrationFailed: any;
    registrationSuccess: any;
    errorMessage: any;
    roles: any;
    selectedRole: any;
    password: any;
    email: any;

    constructor(RestApi) {
        this.RestApi = RestApi;

        this.loading = false;
        this.registrationFailed = false;
        this.registrationSuccess = false;
        this.errorMessage = "";

        this.roles = [{"name": "System Administrator", "internalName": "SYSTEM_ADMINISTRATOR"},
            {"name": "Manager", "internalName": "MANAGER"},
            {"name": "Operator", "internalName": "OPERATOR"},
            {"name": "Business Analyst", "internalName": "BUSINESS_ANALYST"},
            {"name": "Demo User", "internalName": "USER_DEMO"}];

        this.selectedRole = this.roles[0].internalName;
    }


    register() {
        var payload = {};
        payload['password'] = this.password;
        payload['email'] = this.email;
        payload['role'] = this.selectedRole;
        this.loading = true;
        this.registrationFailed = false;
        this.registrationSuccess = false;
        this.errorMessage = "";

        this.RestApi.register(payload)
            .then(response => {
                    this.loading = false;
                    if (response.data.success) {
                        this.registrationSuccess = true;
                    }
                    else {
                        this.registrationFailed = true;
                        this.errorMessage = response.data.notifications[0].title;
                    }

                }, response => { // error

                    this.loading = false;
                    this.registrationFailed = true;
                }
            )
    };
}
;

//RegisterCtrl.$inject = ['RestApi'];