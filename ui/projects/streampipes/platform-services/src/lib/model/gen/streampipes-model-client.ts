/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/* tslint:disable */
/* eslint-disable */
// @ts-nocheck
// Generated using typescript-generator version 3.1.1185 on 2023-05-15 15:33:05.

export class ExtensionsServiceEndpointItem {
    appId: string;
    available: boolean;
    description: string;
    editable: boolean;
    elementId: string;
    includesDocs: boolean;
    includesIcon: boolean;
    installed: boolean;
    name: string;
    streams: ExtensionsServiceEndpointItem[];
    type: string;
    uri: string;

    static fromData(
        data: ExtensionsServiceEndpointItem,
        target?: ExtensionsServiceEndpointItem,
    ): ExtensionsServiceEndpointItem {
        if (!data) {
            return data;
        }
        const instance = target || new ExtensionsServiceEndpointItem();
        instance.appId = data.appId;
        instance.available = data.available;
        instance.description = data.description;
        instance.editable = data.editable;
        instance.elementId = data.elementId;
        instance.includesDocs = data.includesDocs;
        instance.includesIcon = data.includesIcon;
        instance.installed = data.installed;
        instance.name = data.name;
        instance.streams = __getCopyArrayFn(
            ExtensionsServiceEndpointItem.fromData,
        )(data.streams);
        instance.type = data.type;
        instance.uri = data.uri;
        return instance;
    }
}

export class Group {
    groupId: string;
    groupName: string;
    rev: string;
    roles: Role[];

    static fromData(data: Group, target?: Group): Group {
        if (!data) {
            return data;
        }
        const instance = target || new Group();
        instance.groupId = data.groupId;
        instance.groupName = data.groupName;
        instance.rev = data.rev;
        instance.roles = __getCopyArrayFn(__identity<Role>())(data.roles);
        return instance;
    }
}

export class MatchingResultMessage {
    description: string;
    matchingSuccessful: boolean;
    offerSubject: string;
    reasonText: string;
    requirementSubject: string;
    title: string;

    static fromData(
        data: MatchingResultMessage,
        target?: MatchingResultMessage,
    ): MatchingResultMessage {
        if (!data) {
            return data;
        }
        const instance = target || new MatchingResultMessage();
        instance.description = data.description;
        instance.matchingSuccessful = data.matchingSuccessful;
        instance.offerSubject = data.offerSubject;
        instance.reasonText = data.reasonText;
        instance.requirementSubject = data.requirementSubject;
        instance.title = data.title;
        return instance;
    }
}

export class Permission {
    grantedAuthorities: PermissionEntry[];
    objectClassName: string;
    objectInstanceId: string;
    ownerSid: string;
    permissionId: string;
    publicElement: boolean;
    rev: string;

    static fromData(data: Permission, target?: Permission): Permission {
        if (!data) {
            return data;
        }
        const instance = target || new Permission();
        instance.grantedAuthorities = __getCopyArrayFn(
            PermissionEntry.fromData,
        )(data.grantedAuthorities);
        instance.objectClassName = data.objectClassName;
        instance.objectInstanceId = data.objectInstanceId;
        instance.ownerSid = data.ownerSid;
        instance.permissionId = data.permissionId;
        instance.publicElement = data.publicElement;
        instance.rev = data.rev;
        return instance;
    }
}

export class PermissionEntry {
    principalType: PrincipalType;
    sid: string;

    static fromData(
        data: PermissionEntry,
        target?: PermissionEntry,
    ): PermissionEntry {
        if (!data) {
            return data;
        }
        const instance = target || new PermissionEntry();
        instance.principalType = data.principalType;
        instance.sid = data.sid;
        return instance;
    }
}

export class Principal {
    accountEnabled: boolean;
    accountExpired: boolean;
    accountLocked: boolean;
    groups: string[];
    objectPermissions: string[];
    principalId: string;
    principalType: PrincipalType;
    rev: string;
    roles: Role[];
    username: string;

    static fromData(data: Principal, target?: Principal): Principal {
        if (!data) {
            return data;
        }
        const instance = target || new Principal();
        instance.accountEnabled = data.accountEnabled;
        instance.accountExpired = data.accountExpired;
        instance.accountLocked = data.accountLocked;
        instance.groups = __getCopyArrayFn(__identity<string>())(data.groups);
        instance.objectPermissions = __getCopyArrayFn(__identity<string>())(
            data.objectPermissions,
        );
        instance.principalId = data.principalId;
        instance.principalType = data.principalType;
        instance.rev = data.rev;
        instance.roles = __getCopyArrayFn(__identity<Role>())(data.roles);
        instance.username = data.username;
        return instance;
    }
}

export class RawUserApiToken {
    hashedToken: string;
    rawToken: string;
    tokenId: string;
    tokenName: string;

    static fromData(
        data: RawUserApiToken,
        target?: RawUserApiToken,
    ): RawUserApiToken {
        if (!data) {
            return data;
        }
        const instance = target || new RawUserApiToken();
        instance.hashedToken = data.hashedToken;
        instance.rawToken = data.rawToken;
        instance.tokenId = data.tokenId;
        instance.tokenName = data.tokenName;
        return instance;
    }
}

export class ServiceAccount extends Principal {
    clientSecret: string;
    secretEncrypted: boolean;

    static fromData(
        data: ServiceAccount,
        target?: ServiceAccount,
    ): ServiceAccount {
        if (!data) {
            return data;
        }
        const instance = target || new ServiceAccount();
        super.fromData(data, instance);
        instance.clientSecret = data.clientSecret;
        instance.secretEncrypted = data.secretEncrypted;
        return instance;
    }
}

export class UserAccount extends Principal {
    darkMode: boolean;
    fullName: string;
    hideTutorial: boolean;
    password: string;
    preferredDataProcessors: string[];
    preferredDataSinks: string[];
    preferredDataStreams: string[];
    userApiTokens: UserApiToken[];

    static fromData(data: UserAccount, target?: UserAccount): UserAccount {
        if (!data) {
            return data;
        }
        const instance = target || new UserAccount();
        super.fromData(data, instance);
        instance.darkMode = data.darkMode;
        instance.fullName = data.fullName;
        instance.hideTutorial = data.hideTutorial;
        instance.password = data.password;
        instance.preferredDataProcessors = __getCopyArrayFn(
            __identity<string>(),
        )(data.preferredDataProcessors);
        instance.preferredDataSinks = __getCopyArrayFn(__identity<string>())(
            data.preferredDataSinks,
        );
        instance.preferredDataStreams = __getCopyArrayFn(__identity<string>())(
            data.preferredDataStreams,
        );
        instance.userApiTokens = __getCopyArrayFn(UserApiToken.fromData)(
            data.userApiTokens,
        );
        return instance;
    }
}

export class UserApiToken {
    tokenId: string;
    tokenName: string;

    static fromData(data: UserApiToken, target?: UserApiToken): UserApiToken {
        if (!data) {
            return data;
        }
        const instance = target || new UserApiToken();
        instance.tokenId = data.tokenId;
        instance.tokenName = data.tokenName;
        return instance;
    }
}

export type PrincipalType = 'USER_ACCOUNT' | 'SERVICE_ACCOUNT' | 'GROUP';

export type Privilege =
    | 'PRIVILEGE_READ_PIPELINE'
    | 'PRIVILEGE_WRITE_PIPELINE'
    | 'PRIVILEGE_DELETE_PIPELINE'
    | 'PRIVILEGE_READ_ADAPTER'
    | 'PRIVILEGE_WRITE_ADAPTER'
    | 'PRIVILEGE_DELETE_ADAPTER'
    | 'PRIVILEGE_READ_PIPELINE_ELEMENT'
    | 'PRIVILEGE_WRITE_PIPELINE_ELEMENT'
    | 'PRIVILEGE_DELETE_PIPELINE_ELEMENT'
    | 'PRIVILEGE_READ_DASHBOARD'
    | 'PRIVILEGE_WRITE_DASHBOARD'
    | 'PRIVILEGE_DELETE_DASHBOARD'
    | 'PRIVILEGE_READ_DASHBOARD_WIDGET'
    | 'PRIVILEGE_WRITE_DASHBOARD_WIDGET'
    | 'PRIVILEGE_DELETE_DASHBOARD_WIDGET'
    | 'PRIVILEGE_READ_DATA_EXPLORER_VIEW'
    | 'PRIVILEGE_WRITE_DATA_EXPLORER_VIEW'
    | 'PRIVILEGE_DELETE_DATA_EXPLORER_VIEW'
    | 'PRIVILEGE_READ_DATA_EXPLORER_WIDGET'
    | 'PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET'
    | 'PRIVILEGE_DELETE_DATA_EXPLORER_WIDGET'
    | 'PRIVILEGE_READ_APPS'
    | 'PRIVILEGE_WRITE_APPS'
    | 'PRIVILEGE_READ_NOTIFICATIONS'
    | 'PRIVILEGE_READ_FILES'
    | 'PRIVILEGE_WRITE_FILES'
    | 'PRIVILEGE_DELETE_FILES'
    | 'PRIVILEGE_READ_ASSETS'
    | 'PRIVILEGE_WRITE_ASSETS'
    | 'PRIVILEGE_READ_GENERIC_STORAGE'
    | 'PRIVILEGE_WRITE_GENERIC_STORAGE';

export type Role =
    | 'ROLE_ADMIN'
    | 'ROLE_SERVICE_ADMIN'
    | 'ROLE_PIPELINE_ADMIN'
    | 'ROLE_DASHBOARD_ADMIN'
    | 'ROLE_DATA_EXPLORER_ADMIN'
    | 'ROLE_CONNECT_ADMIN'
    | 'ROLE_DASHBOARD_USER'
    | 'ROLE_DATA_EXPLORER_USER'
    | 'ROLE_PIPELINE_USER'
    | 'ROLE_APP_USER'
    | 'ROLE_ASSET_USER'
    | 'ROLE_ASSET_ADMIN';

function __getCopyArrayFn<T>(itemCopyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, itemCopyFn);
}

function __copyArray<T>(array: T[], itemCopyFn: (item: T) => T): T[] {
    return array && array.map(item => item && itemCopyFn(item));
}

function __getCopyObjectFn<T>(
    itemCopyFn: (item: T) => T,
): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, itemCopyFn);
}

function __copyObject<T>(
    object: { [index: string]: T },
    itemCopyFn: (item: T) => T,
): { [index: string]: T } {
    if (!object) {
        return object;
    }
    const result: any = {};
    for (const key in object) {
        if (object.hasOwnProperty(key)) {
            const value = object[key];
            result[key] = value && itemCopyFn(value);
        }
    }
    return result;
}

function __identity<T>(): (value: T) => T {
    return value => value;
}
