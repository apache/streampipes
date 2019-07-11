import 'reflect-metadata';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/fromPromise';
import * as jsonld from 'jsonld';

import {isUndefined} from 'util';

export class TsonLd {

  private context: { [key: string]: string; } = {};
  private classMapping: { [key: string]: any; } = {};

  // private context: {key: string, value: string}[];

  constructor() {
    // this.context = [];

  }

  public addContext(key: string, value: string) {
    this.context[key] = value;
  }

  public addClassMapping(value: any) {
    const key: string = Reflect.getMetadata('RdfsClass', value.prototype.constructor);
    if (isUndefined(key)) {
      console.error('The value parameter of addClassMapping needs the RdfsClass annotation');
    } else {
      this.classMapping[key] = value;
    }
  }

  public toflattenJsonLd(object: any): Observable<{}> {

    const obj = this.toJsonLd(object);
    const context = obj['@context'];
    delete obj['@context'];

    return Observable.fromPromise(new Promise(function (resolve, reject) {
      (jsonld as any).flatten(obj, context, function (err, data) {

        // console.log('flatten data: bla bla bla: ' + JSON.stringify(data, null, 2));
        resolve(data);
      });
    }));
  }

  /**
   *
   * @param object the object to serialize
   * @param {boolean} setContext defines whether the context should be added or not
   * @param {any[]} allIds used to avoid cycles
   * @returns {{}}
   */
  public toJsonLd(object: any, setContext = true, allIds: any[] = []): {} {

    // 1. object to json object
    let jsObject = Object.assign({}, object);
    // console.log('1. Step (js Object): ' + JSON.stringify(jsObject));

    const properties = Object.getOwnPropertyNames(object);


    // 2. Search for key id in object
    let objectIdProperty: any = {};
    for (const property of properties) {
      // TODO add check that just one id is available
      const idProperty = Reflect.getMetadata('RdfsId', object, property);
      if (idProperty === 'rdfsId') {
        objectIdProperty = property;
      }
    }

    if (objectIdProperty === {}) {
      console.error('Error no key Property');
      // TODO generate a key
    }

    if (allIds.includes(jsObject[objectIdProperty])) {
      return {'@id': jsObject[objectIdProperty]};
    } else {
      allIds.push(jsObject[objectIdProperty]);
    }

    // 3. exchange keys with URIs
    for (const property of properties) {

      if (property !== objectIdProperty) {
        const newProperty = Reflect.getMetadata('RdfProperty', object, property);
        // check if the value is an array
        if (Array.isArray(jsObject[property])) {
          jsObject[newProperty] = [];
          if (typeof jsObject[property][0] === 'object') {
            for (const elem of jsObject[property]) {
              jsObject[newProperty].push(this.toJsonLd(elem, false, allIds));
            }
          } else {
            jsObject[newProperty] = jsObject[property];
          }
        } else if (typeof jsObject[property] === 'object') {
          jsObject[newProperty] = this.toJsonLd(jsObject[property], false, allIds);
        } else {
          jsObject[newProperty] = jsObject[property];
        }
      } else {
        jsObject['@id'] = jsObject[property];
      }
      delete jsObject[property];
    }
    // console.log('2. Step (js Object with URIs as key): ' + JSON.stringify(jsObject));

    // 4. add @type to object

    // console.log('3. Step (add type): ' + JSON.stringify(jsObject));
    const objectType = Reflect.getMetadata('RdfsClass', object.constructor);
    jsObject['@type'] = objectType;

    // 5. add @context to object
    if (setContext) {
      const tmp = {};
      tmp['@graph'] = jsObject;
      tmp['@context'] = this.context;
      jsObject = tmp;
    }
    // console.log('testttest ' + JSON.stringify(jsObject));

    return jsObject;
  }

  fromJsonLdType(obj: Object, type: String) {
    let topElement = null;
    const graph = obj['@graph'];
    for (const elem in graph) {
      if (graph[elem]['@type'] === type) {
        topElement = graph[elem];
        obj['@graph'].splice(elem, 1);
      }
    }

    obj['@graph'].unshift(topElement);
    return this.fromJsonLd(obj);
  }



  /**
   *
   * @param {Object} obj
   * @param {T} p
   * @param {{}} ids
   * @returns {any}
   */
  // fromJsonLd<T>(obj: Object, p: T, ids: {}= {}) {
  fromJsonLd(obj: Object, ids: {}= {}, id?: string) {

    const context = obj['@context'];
    let graph = obj['@graph'];


    // Get rdfs type of p
    // const objectType: String = Reflect.getMetadata('RdfsClass', Person);

    // Find first object in graph with this type
    // TODO think what should happen when more of one types are in graph
    let jsonObject = null;
    // for (const o of graph) {
    // const o = graph[0];
    if (graph.length > 0) {
      if (!isUndefined(id)) {
        for (const elem of graph) {
          if (elem['@id'] === id) {
            jsonObject = elem;
          }
        }
        if (isUndefined(jsonObject) || jsonObject == null) {
          console.error('id ' + id + ' is not in graph. this should never happen');
        }
      } else {
        jsonObject = graph[0];
      }
    } else if (!isUndefined(ids[id])) {
      return ids[id];
    } else {
      console.log('graph: ' + graph);
      console.log('FIX BUG: Pass id to fromJSON-LD');
      console.log('id: ' + id);
      console.log('ids: ' + JSON.stringify(ids, null, 2));
    }

    // Create the result object
    const c = this.classMapping[jsonObject['@type']];
    if (isUndefined(c)) {
      console.error('Type: ' + jsonObject['@type'] + ' is not registered in tsonld');
    }
    const result = new c();


    for (const property in jsonObject) {
      let objectProp: string;
      // Remove the @type property
      if (property === '@id') {
        objectProp = Reflect.getMetadata('TsId', c.prototype, property);
      } else {
        objectProp = Reflect.getMetadata('TsProperty', c.prototype, property);
      }

      // skip the @type property
      if (property !== '@type') {

        // check whether property is object or literal
        if (typeof jsonObject[property] === 'object'  && !isUndefined(objectProp)) {


          // check if object or array
          if (Array.isArray(jsonObject[property])) {
            //TODO check if needed
             result[objectProp] = [];
            // ARRAY
            if (jsonObject[property].length > 0 && typeof jsonObject[property][0] === 'object') {

              for (const elem of jsonObject[property]) {

                // 1. check if already deserialized
                if (!isUndefined(ids[elem['@id']])) {
                  // TODO never called

                } else {
                  // 2. deserialize
                  // remove current object from graph
                  const tmpIndex = graph.indexOf(jsonObject, 0);
                  if (tmpIndex > -1) {
                    graph.splice(tmpIndex, 1);
                  }

                  const newObj = {'@context': context, '@graph': graph};

                  ids[jsonObject['@id']] = result;

                  const arrayResult = this.fromJsonLd(newObj, ids, elem['@id']);

                  // TODO hot fix not sure if it works for all cases
                  // graph.splice(0, 1);
                  const index = graph.indexOf(jsonObject, 0);
                  if (index > -1) {
                    graph.splice(index, 1);
                  }
                  result[objectProp].push(arrayResult);

                }
              }
            } else {
              // ARRAY of literals
              result[objectProp] = jsonObject[property];
            }

          } else {
            // NO ARRAY
            // console.log('bbbb ' + jsonObject[property] + ' xxxx ' + Array.isArray(jsonObject[property]));

            // if (Array.isArray(result[objectProp])) {

              // check if already deserialized
              if (!isUndefined(ids[jsonObject[property]['@id']])) {
                // when already desirialized use object from stored array

                // case where array just has one element (this is serialized as an object)

                if (Array.isArray(result[objectProp])) {
                  result[objectProp] = [ids[jsonObject[property]['@id']]];
                  // result[objectProp] = ids[jsonObject[property]['@id']];
                } else {
                  result[objectProp] = ids[jsonObject[property]['@id']];
                }
              } else {
                // if not recursion and add to ids array
                const index = graph.indexOf(jsonObject, 0);
                if (index > -1) {
                  graph.splice(index, 1);
                }
                const newObj = {'@context': context, '@graph': graph};
                ids[jsonObject['@id']] = result;

                // console.log('ddddd: ' + JSON.stringify(ids,null, 2));

                if (!isUndefined(jsonObject[property]['@id'])) {
                  const nestedResult = this.fromJsonLd(newObj, ids, jsonObject[property]['@id']);
                                  // case where array just has one element (this is serialized as an object)

                  console.log(JSON.stringify(result, null, 2));
                  console.log(objectProp);
                if (Array.isArray(result[objectProp])) {

                  result[objectProp] = [nestedResult];
                } else {
                  result[objectProp] = nestedResult;
                }
                }
            }
          }
        } else {
          if (!isUndefined(objectProp)) {
            result[objectProp] = jsonObject[property];
          }
        }
      }
    }

// TODO add handling for and for nested properties

    return result;

  }
  // }

}

function prop<T, K extends keyof T>(obj: T, key: K) {
  return obj[key];
}
