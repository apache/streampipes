import {Injectable} from '@angular/core';

@Injectable()
export class DataTypesService {
  private dataTypes: {label: String, url: String}[]= [
  {
    label: 'String - A textual datatype, e.g., \'machine1\'',
    url: 'http://www.w3.org/2001/XMLSchema#string'},
  {
    label: 'Boolean - A true/false value',
    url: 'http://www.w3.org/2001/XMLSchema#boolean'},
  {
    label:  'Float - A number, e.g., \'1.25\'',
    url: 'http://www.w3.org/2001/XMLSchema#float'
  }];

  constructor() {}

  getLabel(url: String): String {
    for (let i = 0; i < this.dataTypes.length; i++) {
      if (this.dataTypes[i].url === url) {
        return this.dataTypes[i].label;
      }
    }

    return 'Unvalid data type';
  }

  getUrl(id: number): String {
    return this.dataTypes[id].url;
  }

  getDataTypes() {
    return this.dataTypes;
  }

  getNumberTypeUrl(): string {
    return String(this.dataTypes[2].url);
  }

  getStringTypeUrl(): string {
    return String(this.dataTypes[0].url);
  }
  getBooleanTypeUrl(): string {
    return String(this.dataTypes[1].url);
  }

}
