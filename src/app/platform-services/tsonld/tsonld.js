"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
require("reflect-metadata");
var Observable_1 = require("rxjs/Observable");
require("rxjs/add/observable/fromPromise");
var jsonld = require("jsonld");
var util_1 = require("util");
var TsonLd = /** @class */ (function () {
    // private context: {key: string, value: string}[];
    function TsonLd() {
        // this.context = [];
        this.context = {};
        this.classMapping = {};
    }
    TsonLd.prototype.addContext = function (key, value) {
        this.context[key] = value;
    };
    TsonLd.prototype.addClassMapping = function (value) {
        var key = Reflect.getMetadata('RdfsClass', value.prototype.constructor);
        if (util_1.isUndefined(key)) {
            console.error('The value parameter of addClassMapping needs the RdfsClass annotation');
        }
        else {
            this.classMapping[key] = value;
        }
    };
    TsonLd.prototype.toflattenJsonLd = function (object) {
        var obj = this.toJsonLd(object);
        var context = obj['@context'];
        delete obj['@context'];
        return Observable_1.Observable.fromPromise(new Promise(function (resolve, reject) {
            jsonld.flatten(obj, context, function (err, data) {
                // console.log('flatten data: bla bla bla: ' + JSON.stringify(data, null, 2));
                resolve(data);
            });
        }));
    };
    /**
     *
     * @param object the object to serialize
     * @param {boolean} setContext defines whether the context should be added or not
     * @param {any[]} allIds used to avoid cycles
     * @returns {{}}
     */
    TsonLd.prototype.toJsonLd = function (object, setContext, allIds) {
        if (setContext === void 0) { setContext = true; }
        if (allIds === void 0) { allIds = []; }
        // 1. object to json object
        var jsObject = Object.assign({}, object);
        // console.log('1. Step (js Object): ' + JSON.stringify(jsObject));
        var properties = Object.getOwnPropertyNames(object);
        // 2. Search for key id in object
        var objectIdProperty = {};
        for (var _i = 0, properties_1 = properties; _i < properties_1.length; _i++) {
            var property = properties_1[_i];
            // TODO add check that just one id is available
            var idProperty = Reflect.getMetadata('RdfsId', object, property);
            if (idProperty === 'rdfsId') {
                objectIdProperty = property;
            }
        }
        if (objectIdProperty === {}) {
            console.error('Error no key Property');
            // TODO generate a key
        }
        if (allIds.includes(jsObject[objectIdProperty])) {
            return { '@id': jsObject[objectIdProperty] };
        }
        else {
            allIds.push(jsObject[objectIdProperty]);
        }
        // 3. exchange keys with URIs
        for (var _a = 0, properties_2 = properties; _a < properties_2.length; _a++) {
            var property = properties_2[_a];
            if (property !== objectIdProperty) {
                var newProperty = Reflect.getMetadata('RdfProperty', object, property);
                // check if the value is an array
                if (Array.isArray(jsObject[property])) {
                    jsObject[newProperty] = [];
                    if (typeof jsObject[property][0] === 'object') {
                        for (var _b = 0, _c = jsObject[property]; _b < _c.length; _b++) {
                            var elem = _c[_b];
                            jsObject[newProperty].push(this.toJsonLd(elem, false, allIds));
                        }
                    }
                    else {
                        jsObject[newProperty] = jsObject[property];
                    }
                }
                else if (typeof jsObject[property] === 'object') {
                    jsObject[newProperty] = this.toJsonLd(jsObject[property], false, allIds);
                }
                else {
                    jsObject[newProperty] = jsObject[property];
                }
            }
            else {
                jsObject['@id'] = jsObject[property];
            }
            delete jsObject[property];
        }
        // console.log('2. Step (js Object with URIs as key): ' + JSON.stringify(jsObject));
        // 4. add @type to object
        // console.log('3. Step (add type): ' + JSON.stringify(jsObject));
        var objectType = Reflect.getMetadata('RdfsClass', object.constructor);
        jsObject['@type'] = objectType;
        // 5. add @context to object
        if (setContext) {
            var tmp = {};
            tmp['@graph'] = jsObject;
            tmp['@context'] = this.context;
            jsObject = tmp;
        }
        // console.log('testttest ' + JSON.stringify(jsObject));
        return jsObject;
    };
    /**
     *
     * @param {Object} obj
     * @param {T} p
     * @param {{}} ids
     * @returns {any}
     */
    // fromJsonLd<T>(obj: Object, p: T, ids: {}= {}) {
    TsonLd.prototype.fromJsonLd = function (obj, ids, id) {
        if (ids === void 0) { ids = {}; }
        var context = obj['@context'];
        var graph = obj['@graph'];
        // Get rdfs type of p
        // const objectType: String = Reflect.getMetadata('RdfsClass', Person);
        // Find first object in graph with this type
        // TODO think what should happen when more of one types are in graph
        var jsonObject = null;
        // for (const o of graph) {
        var o = graph[0];
        if (graph.length > 0) {
            jsonObject = o;
        }
        else if (!util_1.isUndefined(ids[id])) {
            return ids[id];
        }
        else {
            console.log('graph: ' + graph);
            console.log('FIX BUG: Pass id to fromJSON-LD');
            console.log('id: ' + id);
            console.log('ids: ' + JSON.stringify(ids, null, 2));
        }
        // Create the result object
        var c = this.classMapping[jsonObject['@type']];
        if (util_1.isUndefined(c)) {
            console.error('Type: ' + jsonObject['@type'] + ' is not registered in tsonld');
        }
        var result = new c();
        for (var property in jsonObject) {
            var objectProp = void 0;
            // Remove the @type property
            if (property === '@id') {
                objectProp = Reflect.getMetadata('TsId', c.prototype, property);
            }
            else {
                objectProp = Reflect.getMetadata('TsProperty', c.prototype, property);
            }
            // skip the @type property
            if (property !== '@type') {
                // check whether property is object or literal
                if (typeof jsonObject[property] === 'object') {
                    result[objectProp] = [];
                    // check if object or array
                    if (Array.isArray(jsonObject[property])) {
                        // ARRAY
                        if (jsonObject[property].length > 0 && typeof jsonObject[property][0] === 'object') {
                            for (var _i = 0, _a = jsonObject[property]; _i < _a.length; _i++) {
                                var elem = _a[_i];
                                // 1. check if already deserialized
                                if (!util_1.isUndefined(ids[elem['@id']])) {
                                    // TODO never called
                                }
                                else {
                                    // 2. deserialize
                                    // remove current object from graph
                                    var index = graph.indexOf(jsonObject, 0);
                                    if (index > -1) {
                                        graph.splice(index, 1);
                                    }
                                    var newObj = { '@context': context, '@graph': graph };
                                    ids[jsonObject['@id']] = result;
                                    console.log('jsonObject: ' + JSON.stringify(jsonObject, null, 2));
                                    console.log('elem: ' + JSON.stringify(elem, null, 2));
                                    console.log('id: ' + jsonObject['@id']);
                                    var arrayResult = this.fromJsonLd(newObj, ids, elem['@id']);
                                    // TODO hot fix not sure if it works for all cases
                                    graph.splice(0, 1);
                                    result[objectProp].push(arrayResult);
                                }
                            }
                        }
                        else {
                            // ARRAY of literals
                            result[objectProp] = jsonObject[property];
                        }
                    }
                    else {
                        // NO ARRAY
                        // console.log('bbbb ' + jsonObject[property] + ' xxxx ' + Array.isArray(jsonObject[property]));
                        // check if already deserialized
                        if (!util_1.isUndefined(ids[jsonObject[property]['@id']])) {
                            // when already desirialized use object from stored array
                            result[objectProp] = ids[jsonObject[property]['@id']];
                        }
                        else {
                            // if not recursion and add to ids array
                            var index = graph.indexOf(jsonObject, 0);
                            if (index > -1) {
                                graph.splice(index, 1);
                            }
                            var newObj = { '@context': context, '@graph': graph };
                            ids[jsonObject['@id']] = result;
                            console.log('ddddd: ' + JSON.stringify(ids, null, 2));
                            var nestedResult = this.fromJsonLd(newObj, ids);
                            result[objectProp] = nestedResult;
                        }
                    }
                }
                else {
                    result[objectProp] = jsonObject[property];
                }
            }
        }
        // TODO add handling for and for nested properties
        return result;
    };
    return TsonLd;
}());
exports.TsonLd = TsonLd;
function prop(obj, key) {
    return obj[key];
}
