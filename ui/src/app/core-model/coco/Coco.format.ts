import { Image } from "./Image";
import { Annotation } from "./Annotation";
import { Category } from "./Category";

export class CocoFormat {


  info : {
    "year": number,
    "version": String,
    "description": String,
    "contributor": String,
    "url": String,
    "date_created": Date,
  };
  licenses: [];
  images: Image[] = [];
  annotations: Annotation[] = [];
  categories: Category[] = [];

  constructor(file_name, width, height) {
    let image = new Image();
    image.file_name = file_name;
    image.height = height;
    image.width = width;
    this.images.push(image)
  }

  getLabelById(id) {
    return this.categories.find(elem => elem.id == id).name;
  }

  getLabelId(name, supercategory): number {
    let category = this.categories.find(elem => elem.name == name && elem.supercategory == supercategory);
    if (category === undefined) {
      category = new Category(this.categories.length + 1, name, supercategory);
      this.categories.push(category)
    }
    return category.id;
  }

  addReactAnnotation(x, y, width, height, categoryId) {
    let annnotation = new Annotation();
    annnotation.id = this.annotations.length + 1;
    annnotation.iscrowd = 0;
    annnotation.image_id = 1;
    annnotation.bbox = [x, y, width, height];
    annnotation.category_id = categoryId;
    this.annotations.push(annnotation)
  }

  removeAnnotation(id) {
    this.annotations = this.annotations.filter(anno => anno.id !== id);
  }

}