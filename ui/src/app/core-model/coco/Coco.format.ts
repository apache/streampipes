import { Annotation } from './Annotation';
import { Category } from './Category';
import { Image } from './Image';

export class CocoFormat {

  info: {
    'year': number,
    'version': string,
    'description': string,
    'contributor': string,
    'url': string,
    'date_created': Date,
  };
  licenses: [];
  images: Image[] = [];
  annotations: Annotation[] = [];
  categories: Category[] = [];

  constructor() { }

}
