import { Injectable } from '@angular/core';

@Injectable()
export class IconService {

  constructor(
  ) {}

     getBase64(file: File) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
        });
    }

  toBase64(file: File) {
      return this.getBase64(file);
  }

}
