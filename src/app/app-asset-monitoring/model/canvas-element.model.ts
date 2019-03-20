import {CanvasAttributes} from "./canvas-attributes.model";

export interface CanvasElement {
    attrs: CanvasAttributes;
    className: string;
    children: CanvasElement[];
}