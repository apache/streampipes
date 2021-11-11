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

import { User } from '../model/User';
import { UserRole } from '../../../src/app/_enums/user-role.enum';

export class UserBuilder {
    user: User;

    constructor(email: string) {
        this.user = new User();
        this.user.email = email;
    }

    public static create(email: string) {
        return new UserBuilder(email);
    }

    public setName(name: string) {
        this.user.name = name;
        return this;
    }

    public setPassword(password: string) {
        this.user.password = password;
        return this;
    }

    public addRole(role: UserRole) {
        this.user.role.push(role);
        return this;
    }

    build() {
        return this.user;
    }
}
