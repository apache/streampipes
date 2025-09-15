import { MatPaginatorIntl } from '@angular/material/paginator';

export function getCustomPaginatorIntl(): MatPaginatorIntl {
    const paginatorIntl = new MatPaginatorIntl();

    paginatorIntl.itemsPerPageLabel = 'Items per page:';
    paginatorIntl.nextPageLabel = 'Next';
    paginatorIntl.previousPageLabel = 'Previous';

    paginatorIntl.getRangeLabel = () => '';

    return paginatorIntl;
}
