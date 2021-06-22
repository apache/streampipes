import {login} from './general/login';
import {logout} from './general/logout';
import {dataCy} from './general/dataCy';
import 'cypress-file-upload';

// General commands
Cypress.Commands.add('login', login);
Cypress.Commands.add('logout', logout);
Cypress.Commands.add('dataCy', dataCy);


