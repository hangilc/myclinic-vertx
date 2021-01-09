
export function numberOfPages(items, itemsPerPage){
    return Math.trunc((items + itemsPerPage - 1) / itemsPerPage);
}