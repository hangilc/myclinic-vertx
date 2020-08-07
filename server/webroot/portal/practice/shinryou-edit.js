import {Component} from "./component.js";

export class ShinryouEdit extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.labelElement = map.label;
        this.deleteElement = map.delete;
        this.cancelElement = map.cancel;
        this.editTekiyouElement = map.editTekiyou;
    }

    init(shinryouFull){
        this.shinryouId = shinryouFull.shinryou.shinryouId;
        this.labelElement.text(shinryouFull.master.name);
        this.deleteElement.on("click", async event => {
            await this.rest.deleteShinryou(shinryouFull.shinryou.shinryouId);
            this.ele.trigger("deleted");
        });
        this.cancelElement.on("click", event => this.ele.trigger("cancel"));
        this.editTekiyouElement.on("click", event => this.doEditTekiyou());
    }

    onCancel(cb){
        this.ele.on("cancel", cb);
    }

    onDeleted(cb){
        this.ele.on("deleted", cb);
    }

    async doEditTekiyou(){
        if( this.shinryouId ){
            let result = prompt("適用の入力", this.getCurrentTekiyou());
            if( result === null ){
                return;
            }
            await this.rest.setShinryouTekiyou(this.shinryouId, result);
        }
    }

    getCurrentTekiyou(){
        return "";
    }

}