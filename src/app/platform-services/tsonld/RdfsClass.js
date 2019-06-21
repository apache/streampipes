"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function RdfsClass(propertyName) {
    return function (target) {
        Reflect.defineMetadata('RdfsClass', propertyName, target);
    };
}
exports.RdfsClass = RdfsClass;
