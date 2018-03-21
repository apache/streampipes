import {Injectable} from '@angular/core';

@Injectable()
export class DataTypesService {
  private dataTypes: [{label: String, url: String}]= [
  {
    label: 'String - A textual datatype, e.g., \'machine1\'',
    url: 'http://www.w3.org/2001/XMLSchema#string'},
  {
    label: 'Boolean - A true/false value',
    url: 'http://www.w3.org/2001/XMLSchema#boolean'},
  {
    label:  'Number - A number, e.g., \'1.25\'',
    url: 'http://schema.org/Number'
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
}
