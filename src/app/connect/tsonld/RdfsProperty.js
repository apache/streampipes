"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function RdfProperty(propertyName) {
    return function (target, key) {
        Reflect.defineMetadata('RdfProperty', propertyName, target, key);
        Reflect.defineMetadata('TsProperty', key, target, propertyName);
    };
}
exports.RdfProperty = RdfProperty;
