import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filter'
})

export class FilterPipe implements PipeTransform {



    transform(adapterDescription: any, filterTerm?: any): any{
        //check if search filterTerm is undefined
        if(filterTerm == undefined) return adapterDescription;
        return adapterDescription.filter(function(adapterDescription){
            adapterDescription.label.replace(' ', '_');
            if (adapterDescription.label.toLowerCase().includes(filterTerm.toLowerCase()) || adapterDescription.description.toLowerCase().includes(filterTerm.toLowerCase())) {
                return true
            }
            else {
                return false
            }
        });

    }

}