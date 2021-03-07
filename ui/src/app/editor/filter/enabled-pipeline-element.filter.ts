import {Pipe, PipeTransform} from "@angular/core";
import {PipelineElementConfig} from "../model/editor.model";

@Pipe({
  name: 'enabledPipelineElement',
  pure: false
})
export class EnabledPipelineElementFilter implements PipeTransform {
  transform(items: PipelineElementConfig[]): any {
    if (!items) {
      return items;
    }
    return items.filter(item => item.settings.disabled == undefined || !item.settings.disabled);
  }
}
