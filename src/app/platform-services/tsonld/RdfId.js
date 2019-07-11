"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function RdfId(target, key) {
    Reflect.defineMetadata('RdfsId', 'rdfsId', target, key);
    Reflect.defineMetadata('TsId', key, target, '@id');
}
exports.RdfId = RdfId;
