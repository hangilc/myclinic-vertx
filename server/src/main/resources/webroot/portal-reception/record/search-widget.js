import {Widget} from "../../js/widget.js";
import {show, hide, submit} from "../../js/dom-helper.js";
import {Item} from "./item.js";
import {Nav} from "../../components/nav.js";
import {numberOfPages} from "../../js/general-util.js";

const bodyTmpl = `
    <form class="form-inline x-form" onsubmit="return false;">
        <input type="text" class="form-control mr-2 x-search-text" style="width: 60px; flex-grow: 1"/>
        <buttn type="submit" class="btn btn-primary btn-sm">検索</buttn>
    </form>
    <div class="d-none x-nav"></div>
    <div class="x-list"></div>
`;

export class SearchWidget extends Widget{
    constructor(rest) {
        super();
        this.props = {
            patients: [],
            page: 0,
            totalPages: 0
        };
        this.itemsPerPage = 10;
        this.rest = rest;
        this.setTitle("患者検索");
        const bmap = this.bmap = this.setBody(bodyTmpl);
        this.nav = new Nav(bmap.nav);
        this.nav.setTriggerFun(page => {
            this.props.page = page;
            this.updateUI();
        });
        submit(bmap.form, async event => {
            await this.doSearch();
            this.updateUI();
        });
    }

    initFocus(){
        this.bmap.searchText.focus();
    }

    async doSearch(){
        const text = this.bmap.searchText.value.trim();
        if( !text ){
            return;
        }
        this.props.patients = await this.rest.searchPatient(text);
        this.props.page = 0;
        this.props.totalPages = numberOfPages(this.props.patients.length, this.itemsPerPage);
    }

    updateUI(){
        this.updateNavUI();
        this.updateListUI();
    }

    updateNavUI(){
        if( this.props.totalPages > 1 ){
            this.nav.adaptToPage(this.props.page, this.props.totalPages);
            show(this.bmap.nav);
        } else {
            hide(this.bmap.nav);
        }
    }

    updateListUI(){
        const patients = this.props.patients.slice(
            this.props.page * this.itemsPerPage,
            (this.props.page + 1) * this.itemsPerPage);
        this.bmap.list.innerHTML = "";
        patients.forEach(patient => {
            const item = new Item(patient);
            this.bmap.list.append(item.ele);
        });
    }

}
