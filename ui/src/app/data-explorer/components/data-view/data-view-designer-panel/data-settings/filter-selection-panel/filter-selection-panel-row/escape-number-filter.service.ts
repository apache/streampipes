import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class EscapeNumberFilterService {
    // Method to remove enclosing double quotes
    removeEnclosingQuotes(value: string): string {
        return value?.replace(/^"|"$/g, '');
    }

    // Updates the filter value based on the field type and input value.
    // Ensures that numeric values are wrapped in double quotes to prevent parsing issues on the backend.
    // This check is necessary because the filter value is transmitted as a triple [field, operator, value],
    // which causes the type information to be lost. Once the API is changed to retain type information,
    // this service can be removed.
    escapeIfNumberValue(
        filter: any,
        value: string,
        tagValues: Map<string, string[]>,
    ): string {
        const isTagValueKey = this.checkIfFilterOnTagValue(filter, tagValues);
        const isNumericValue = this.checkIfNumericalValue(value);

        if (isNumericValue && (isTagValueKey || !filter?.field?.numeric)) {
            return `"${value}"`;
        } else {
            return value;
        }
    }

    private checkIfFilterOnTagValue(
        filter: any,
        tagValues: Map<string, string[]>,
    ): boolean {
        return (
            tagValues.has(filter?.field?.runtimeName) &&
            tagValues.get(filter.field.runtimeName)?.length > 0
        );
    }

    private checkIfNumericalValue(value: string): boolean {
        return !isNaN(Number(value));
    }
}
