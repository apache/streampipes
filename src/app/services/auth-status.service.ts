import { Injectable } from '@angular/core';

@Injectable()
export class AuthStatusService {

    email: string;
    username: string;
    token: string;
    authenticated: boolean = false;
    configured: boolean = false;

    constructor() {
    }

}
