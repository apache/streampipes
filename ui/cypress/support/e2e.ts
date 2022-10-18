/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { login } from './general/login';
import { logout } from './general/logout';
import { dataCy } from './general/dataCy';
import { switchUser } from './general/switchUser';
// tslint:disable-next-line:no-implicit-dependencies
import 'cypress-file-upload';
import { resetStreamPipes } from './general/resetStreamPipes';
import { initStreamPipesTest } from './general/InitStreamPipesTest';
import { removeDownloadDirectory } from './general/removeDownloadDirectory';

// General commands
Cypress.Commands.add('login', login);
Cypress.Commands.add('logout', logout);
Cypress.Commands.add('switchUser', switchUser);
Cypress.Commands.add('dataCy', dataCy);
Cypress.Commands.add('resetStreamPipes', resetStreamPipes);
Cypress.Commands.add('initStreamPipesTest', initStreamPipesTest);
Cypress.Commands.add('removeDownloadDirectory', removeDownloadDirectory);
