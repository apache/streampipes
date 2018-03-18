import * as angular from 'angular';

class SelectFilter {

    static selectFilter() {
        return (pipelineElements, selectedOptions) => {
            var filteredElements = [];
            angular.forEach(pipelineElements, pe => {
                if (pe.category.length === 0) {
                    filteredElements.push(pe);
                }
                angular.forEach(selectedOptions, so => {
                    if (pe.category.indexOf(so) !== -1) {
                        filteredElements.push(pe);
                    }
                })
            })
            return filteredElements;
        }
    }
}

export default SelectFilter.selectFilter;

