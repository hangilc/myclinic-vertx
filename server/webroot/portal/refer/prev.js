import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";
import {parseElement} from "../js/parse-element.js";

let filePattern = /\d+-refer-(\d{4})(\d{2})(\d{2}).*\.json/;

let itemTemplate = `
    <tr>
        <td><label class="x-date col-form-label"></label></td>
        <td>
            <button class="btn btn-success x-copy">コピー</button>
            <button class="btn btn-link x-delete">削除</button>
        </td>
    </tr>
`;

export class Prev extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.tbodyElement = ele.find("tbody");
    }

    init(){
        super.init();
        return this;
    }

    set(patientId, prevs){
        super.set();
        this.setPrevs(patientId, prevs);
        return this;
    }

    setPrevs(patientId, prevs){
        this.tbodyElement.html("");
        for(let prev of prevs){
            let m = prev.match(filePattern);
            if( m ){
                let sqldate = `${m[1]}-${m[2]}-${m[3]}`;
                let rep = kanjidate.sqldateToKanji(sqldate, {padZero: true});
                let item = $(itemTemplate);
                let map = parseElement(item);
                map.date.text(rep);
                map.copy.on("click", event => this.doCopy(patientId, prev));
                map.delete.on("click", event => this.doDelete(patientId, prev));
                this.tbodyElement.append(item);
            }
        }
    }

    onCopy(cb){
        this.on("copy", (event, data) => cb(data));
    }

    async doCopy(patientId, prev){
        let data = await this.rest.getRefer(patientId, prev);
        this.trigger("copy", data);
    }

    onDeleted(cb){
        this.on("deleted", event => cb());
    }

    async doDelete(patientId, prev){
        if( confirm("この紹介状の記録を本当に削除していいですか？") ){
            await this.rest.deleteRefer(patientId, prev);
            this.trigger("deleted");
        }
    }
}
