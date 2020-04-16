export class Category {


  constructor(id: number, name: String, supercategory: String) {
    this.id = id;
    this.name = name;
    this.supercategory = supercategory;
  }

  id: number;
  name: String;
  supercategory: String;
}