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

/* tslint:disable */
/* eslint-disable */
// @ts-nocheck
// Generated using typescript-generator version 2.27.744 on 2021-10-05 10:08:33.

export class Element {
    elementId: string;
    publicElement: boolean;

    static fromData(data: Element, target?: Element): Element {
        if (!data) {
            return data;
        }
        const instance = target || new Element();
        instance.elementId = data.elementId;
        instance.publicElement = data.publicElement;
        return instance;
    }
}

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

    static fromData(data: ExtensionsServiceEndpointItem, target?: ExtensionsServiceEndpointItem): ExtensionsServiceEndpointItem {
        if (!data) {
            return data;
        }
        const instance = target || new ExtensionsServiceEndpointItem();
        instance.name = data.name;
        instance.description = data.description;
        instance.elementId = data.elementId;
        instance.uri = data.uri;
        instance.type = data.type;
        instance.appId = data.appId;
        instance.includesIcon = data.includesIcon;
        instance.includesDocs = data.includesDocs;
        instance.installed = data.installed;
        instance.editable = data.editable;
        instance.available = data.available;
        instance.streams = __getCopyArrayFn(ExtensionsServiceEndpointItem.fromData)(data.streams);
        return instance;
    }
}

export class FileMetadata {
    createdAt: number;
    createdByUser: string;
    fileId: string;
    filetype: string;
    internalFilename: string;
    lastModified: number;
    originalFilename: string;
    rev: string;

    static fromData(data: FileMetadata, target?: FileMetadata): FileMetadata {
        if (!data) {
            return data;
        }
        const instance = target || new FileMetadata();
        instance.fileId = data.fileId;
        instance.rev = data.rev;
        instance.internalFilename = data.internalFilename;
        instance.originalFilename = data.originalFilename;
        instance.filetype = data.filetype;
        instance.createdAt = data.createdAt;
        instance.lastModified = data.lastModified;
        instance.createdByUser = data.createdByUser;
        return instance;
    }
}

export interface GrantedAuthority {
    authority: string;
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
        instance.rev = data.rev;
        instance.groupName = data.groupName;
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

    static fromData(data: MatchingResultMessage, target?: MatchingResultMessage): MatchingResultMessage {
        if (!data) {
            return data;
        }
        const instance = target || new MatchingResultMessage();
        instance.matchingSuccessful = data.matchingSuccessful;
        instance.title = data.title;
        instance.description = data.description;
        instance.offerSubject = data.offerSubject;
        instance.requirementSubject = data.requirementSubject;
        instance.reasonText = data.reasonText;
        return instance;
    }
}

export class Principal implements UserDetails {
    accountEnabled: boolean;
    accountExpired: boolean;
    accountLocked: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    authorities: GrantedAuthority[];
    credentialsNonExpired: boolean;
    enabled: boolean;
    groups: string[];
    ownActions: Element[];
    ownSepas: Element[];
    ownSources: Element[];
    password: string;
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
        instance.enabled = data.enabled;
        instance.username = data.username;
        instance.password = data.password;
        instance.accountNonExpired = data.accountNonExpired;
        instance.accountNonLocked = data.accountNonLocked;
        instance.credentialsNonExpired = data.credentialsNonExpired;
        instance.authorities = __getCopyArrayFn(__identity<GrantedAuthority>())(data.authorities);
        instance.principalId = data.principalId;
        instance.rev = data.rev;
        instance.accountEnabled = data.accountEnabled;
        instance.accountLocked = data.accountLocked;
        instance.accountExpired = data.accountExpired;
        instance.ownSources = __getCopyArrayFn(Element.fromData)(data.ownSources);
        instance.ownSepas = __getCopyArrayFn(Element.fromData)(data.ownSepas);
        instance.ownActions = __getCopyArrayFn(Element.fromData)(data.ownActions);
        instance.roles = __getCopyArrayFn(__identity<Role>())(data.roles);
        instance.groups = __getCopyArrayFn(__identity<string>())(data.groups);
        instance.principalType = data.principalType;
        return instance;
    }
}

export class RawUserApiToken {
    hashedToken: string;
    rawToken: string;
    tokenId: string;
    tokenName: string;

    static fromData(data: RawUserApiToken, target?: RawUserApiToken): RawUserApiToken {
        if (!data) {
            return data;
        }
        const instance = target || new RawUserApiToken();
        instance.rawToken = data.rawToken;
        instance.hashedToken = data.hashedToken;
        instance.tokenName = data.tokenName;
        instance.tokenId = data.tokenId;
        return instance;
    }
}

export class ServiceAccount extends Principal {
    clientSecret: string;

    static fromData(data: ServiceAccount, target?: ServiceAccount): ServiceAccount {
        if (!data) {
            return data;
        }
        const instance = target || new ServiceAccount();
        super.fromData(data, instance);
        instance.clientSecret = data.clientSecret;
        return instance;
    }
}

export class UserAccount extends Principal {
    darkMode: boolean;
    email: string;
    fullName: string;
    hideTutorial: boolean;
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
        instance.email = data.email;
        instance.fullName = data.fullName;
        instance.preferredDataStreams = __getCopyArrayFn(__identity<string>())(data.preferredDataStreams);
        instance.preferredDataProcessors = __getCopyArrayFn(__identity<string>())(data.preferredDataProcessors);
        instance.preferredDataSinks = __getCopyArrayFn(__identity<string>())(data.preferredDataSinks);
        instance.userApiTokens = __getCopyArrayFn(UserApiToken.fromData)(data.userApiTokens);
        instance.hideTutorial = data.hideTutorial;
        instance.darkMode = data.darkMode;
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

export interface UserDetails {
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    authorities: GrantedAuthority[];
    credentialsNonExpired: boolean;
    enabled: boolean;
    password: string;
    username: string;
}

export class UserInfo {
    darkMode: boolean;
    displayName: string;
    email: string;
    roles: string[];
    showTutorial: boolean;
    username: string;

    static fromData(data: UserInfo, target?: UserInfo): UserInfo {
        if (!data) {
            return data;
        }
        const instance = target || new UserInfo();
        instance.username = data.username;
        instance.displayName = data.displayName;
        instance.email = data.email;
        instance.roles = __getCopyArrayFn(__identity<string>())(data.roles);
        instance.showTutorial = data.showTutorial;
        instance.darkMode = data.darkMode;
        return instance;
    }
}

export type PrincipalType = "USER_ACCOUNT" | "SERVICE_ACCOUNT";

export type Role = "ADMIN" | "PIPELINE_ADMIN" | "DASHBOARD_ADMIN" | "DATA_EXPLORER_ADMIN" | "CONNECT_ADMIN" | "DASHBOARD_USER" | "DATA_EXPLORER_USER" | "PIPELINE_USER" | "APP_USER";

function __getCopyArrayFn<T>(itemCopyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, itemCopyFn);
}

function __copyArray<T>(array: T[], itemCopyFn: (item: T) => T): T[] {
    return array && array.map(item => item && itemCopyFn(item));
}

function __getCopyObjectFn<T>(itemCopyFn: (item: T) => T): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, itemCopyFn);
}

function __copyObject<T>(object: { [index: string]: T }, itemCopyFn: (item: T) => T): { [index: string]: T } {
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
