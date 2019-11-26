/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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