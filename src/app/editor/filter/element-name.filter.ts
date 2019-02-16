import * as angular from 'angular';

class ElementNameFilter {

    static elementNameFilter() {
        return (pipelineElements, elementName) => {
            if (!elementName || elementName === "") {
                return pipelineElements;
            } else {
                var filteredElements = [];
                angular.forEach(pipelineElements, pe => {
                    if (pe.name.toLowerCase().indexOf(elementName.toLowerCase()) !== -1) {
                        filteredElements.push(pe);
                    }
                })
                return filteredElements;
            }
        }
    }
}

export default ElementNameFilter.elementNameFilter;

