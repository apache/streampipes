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

  addImage(fileName) {
      const image = new Image();
      image.file_name = fileName;
      image.id = this.images.length + 1;
      this.images.push(image);
  }

  getLabelById(id) {
    return this.categories.find(elem => elem.id === id).name;
  }

  getLabelId(supercategory, name): number {
    let category = this.categories.find(elem => elem.name === name && elem.supercategory === supercategory);
    if (category === undefined) {
      category = new Category(this.categories.length + 1, name, supercategory);
      this.categories.push(category);
    }
    return category.id;
  }

  addReactAnnotationToFirstImage(cords, size, supercategory, category): Annotation {
    const annotation = new Annotation();
    annotation.id = this.annotations.length + 1;
    annotation.iscrowd = 0;
    annotation.image_id = 1;
    annotation.bbox = [cords.x, cords.y, size.x, size.y];
    annotation.category_id = this.getLabelId(supercategory, category);
    annotation.category_name = category;
    this.annotations.push(annotation);
    return annotation;
  }

  addPolygonAnnotationFirstImage(points, supercategory, category): Annotation {
    const annotation = new Annotation();
    annotation.id = this.annotations.length + 1;
    annotation.iscrowd = 0;
    annotation.image_id = 1;
    annotation.segmentation = [points];
    annotation.category_id = this.getLabelId(supercategory, category);
    annotation.category_name = category;    this.annotations.push(annotation);
    return annotation;
  }

  addBrushAnnotationFirstImage(points, brushSize, supercategory, category): Annotation {
    const annotation = new Annotation();
    annotation.id = this.annotations.length + 1;
    annotation.iscrowd = 0;
    annotation.image_id = 1;
    annotation.segmentation = [points];
    annotation.brushSize = brushSize;
    annotation.category_id = this.getLabelId(supercategory, category);
    annotation.category_name = category;
    this.annotations.push(annotation);
    return annotation;
  }

  removeAnnotation(id) {
    this.annotations = this.annotations.filter(anno => anno.id !== id);
  }

}
