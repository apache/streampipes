import {Injectable} from '@angular/core';
import * as jsonpatch from 'fast-json-patch';

/**
 * genaue Dokumentation für JSON Service im Java-Projekt.
 * GitLab --> streamconnect --> json-transforming
 */

@Injectable()
export class WriteJsonService {

  private static instance: WriteJsonService = new WriteJsonService();

  private document = JSON.parse('{}');

  constructor() {
    if (WriteJsonService.instance) {
      throw new Error('Error: Instantiation failed : Use getInstance() instead of new.');
    }
  }

  mapMove = new Map<string, string>();
  mapAddDelete = new Map<string, string>();

  public static getInstance(): WriteJsonService {
    return WriteJsonService.instance;
  }

  // works under constraint that key in HashMap is unique
  public add(key: string, value: string): void {
    const mapKey = this.lastID(key);
    if (!this.mapAddDelete.has(mapKey)) {
      const op = '{ "op": "add", "path": "' + key + '", "value": "' + value + '" }, ';
      console.log(op);
      this.mapAddDelete.set(mapKey, op);
    } else {
      this.mapAddDelete.delete(mapKey);
    }
  }

  // same for here: no controll, wheter key is removed more than once --> must be controlled in caller
  public remove(key: string): void {
    const mapKey = this.lastID(key);
    if (!this.mapAddDelete.has(mapKey)) {
      const op = '{ "op": "remove", "path": "' + key + '" }, ';
      this.mapAddDelete.set(mapKey, op);
      this.mapMove.delete(mapKey);
    } else {
      this.mapAddDelete.delete(mapKey);
    }
  }

  public move(depart: string, target: string): void {
    const departKey = this.lastID(depart);
    const op = '{ "op": "move", "from": "' + depart + '", "path": "' + target + '" }, ';

    if (this.mapMove.has(departKey)) {
      this.mapMove.delete(departKey);
    }
    this.mapMove.set(departKey, op);
    console.log(op);
  }

  private lastID(key: string): string {
    const index = key.lastIndexOf('/');
    return key.substring(index + 1);
  }

  public patcher(): void {
    const new_document = {};
    let patch = '';
    let allPatch;

    this.mapAddDelete.forEach(value => {
      patch += value;
    });

    this.mapMove.forEach(value => {
      patch += value;
    });

    // [] nicht vergessen
    if (patch.length >= 2) {
      allPatch = '[' + patch.substring(0, patch.length - 2) + ']';
      console.log(allPatch);
    } else {
      console.log('nothing to patch');
    }
  }
}
