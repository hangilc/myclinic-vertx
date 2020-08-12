import {parseElement} from "../js/parse-element.js";

let html = `
<a href="javascript:void(0)" class="x-first">最初</a>
<a href="javascript:void(0)" class="x-prev ml-1">前へ</a>
<a href="javascript:void(0)" class="x-next ml-1">次へ</a>
<a href="javascript:void(0)" class="x-last ml-1 mr-1">最後</a>
[<span class="x-page"></span>/<span class="x-total"></span>]
`;

export function populateRecordNav(ele, onPageChanged){
    ele.classList.add("record-nav", "hidden");
    ele.innerHTML = html;
    let map = parseElement(ele);
    let currentPage = 0;
    let totalPages = 0;
    adaptUI(ele, map, currentPage, totalPages);
    map.first.onclick = event => gotoPage(ele, 0, totalPages);
    map.prev.onclick = event => gotoPage(ele, currentPage - 1, totalPages);
    map.next.onclick = event => gotoPage(ele, currentPage + 1, totalPages);
    map.last.onclick = event => gotoPage(ele, totalPages -1, totalPages);
    onPageChanged((newCurrentPage, newTotalPages) => {
        currentPage = newCurrentPage;
        totalPages = newTotalPages;
        adaptUI(ele, map, currentPage, totalPages);
    });
}

function gotoPage(ele, page, totalPages){
    if( page >= 0 && page < totalPages ) {
        ele.dispatchEvent(new CustomEvent("goto-page", {bubbles: true, detail: page}));
    }
}

function adaptUI(ele, map, currentPage, totalPages){
    if( totalPages >= 2 ){
        ele.classList.remove("hidden");
        map.page.innerText = `${currentPage+1}`;
        map.total.innerText = `${totalPages}`;
    } else {
        ele.classList.add("hidden");
        map.page.innerText = "";
        map.total.innerText = "";
    }
}
