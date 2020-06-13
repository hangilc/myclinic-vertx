import {Component} from "./component.js";

export class DiseaseSearch extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.formElement = map.form;
        this.searchTextElement = map.searchText;
        this.selectElement = map.select;
    }

    init(){
        this.formElement.on("submit", event => { this.doSearch(); return false; });
        this.selectElement.on("change", event => this.doSelectionChanged());
    }

    set(date){
        this.date = date;
    }

    getSearchKind(){
        return this.formElement.find("input[type=radio][name=search-kind]:checked").val();
    }

    onByoumeiMasterSelected(cb){
        this.on("byoumei_master_selected", (event, master) => cb(event, master));
    }

    onShuushokugoMasterSelected(cb){
        this.on("shuushokugo_master_selected", (event, master) => cb(event, master));
    }

    async doExampleSelected(ex){

    }

    async doSelectionChanged(){
        let opt = this.selectElement.find("option:selected");
        let kind = opt.data("kind");
        if( kind === "byoumei" ){
            let master = opt.data("master");
            this.trigger("byoumei_master_selected", master);
        } else if( kind === "adj" ){
            let master = opt.data("master");
            this.trigger("shuushokugo_master_selected", master);
        } else if( kind === "example" ){
            let ex = opt.data("data");
            await this.doExampleSelected(ex);
        }
        this.setName(this.data.getRep());

    }

    async doSearch(){
        let text = this.searchTextElement.val();
        let date = this.date;
        let searchKind = this.getSearchKind();
        console.log("searchKind", searchKind);
        if( searchKind === "byoumei" ){
            let result = await this.rest.searchByoumeiMaster(text, date);
            this.selectElement.html("");
            for(let m of result){
                let opt = $("<option>");
                opt.text(m.name);
                opt.data("master", m);
                opt.data("kind", searchKind);
                this.selectElement.append(opt);
            }
        } else if( searchKind === "adj" ){
            let result = await this.rest.searchShuushokugoMaster(text, date);
            this.selectElement.html("");
            for(let m of result){
                let opt = $("<option>");
                opt.text(m.name);
                opt.data("master", m);
                opt.data("kind", searchKind);
                this.selectElement.append(opt);
            }
        }
    }
}