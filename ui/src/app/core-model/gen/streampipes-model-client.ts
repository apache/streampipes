/* tslint:disable */
/* eslint-disable */
// @ts-nocheck
// Generated using typescript-generator version 2.24.612 on 2020-09-23 15:34:40.

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
