import {Component} from "./component.js";
import * as kanjidate from "../js/kanjidate.js";

let template = `
    <tr>
        <td class="x-recipient"></td>
        <td class="x-from-date"></td>
        <td class="x-upto-date"></td>
        <td class="form-inline">
           <button type="button" class="btn btn-success x-copy">複製</button> 
           <button type="button" class="btn btn-link x-delete">削除</button> 
        </td>
    </tr>
`;

export class HistoryItem extends Component {
    constructor(history){
        super($(template));
        let {stamp, data} = history;
        let map = this.getMap(this.ele);
        map.recipient.text(data.recipient);
        map.fromDate.text(fromDateOfData(data));
        map.uptoDate.text(uptoDateOfData(data));
        this.setClickHandler(map.copy, () => this.doCopy());
        this.setClickHandler(map.delete, () => this.doDelete());
    }

    onCopy(cb){
        this.on("copy", () => cb());
    }

    doCopy(){
        this.trigger("copy");
    }

    async doDelete(){

    }
}

function fromDateOfData(data) {
    let nen = parseInt(data["subtitle1.from.nen"]);
    let month = parseInt(data["subtitle1.from.month"]);
    let day = parseInt(data["subtitle1.from.day"]);
    if (!isNaN(nen) && !isNaN(month) && !isNaN(day)) {
        let year = kanjidate.gengouToSeireki("令和", nen);
        let sqldate = kanjidate.toSqldate(year, month, day);
        return kanjidate.sqldateToKanji(sqldate, {padZero: true});
    }
    return null;
}

function uptoDateOfData(data) {
    let nen = parseInt(data["subtitle1.upto.nen"]);
    let month = parseInt(data["subtitle1.upto.month"]);
    let day = parseInt(data["subtitle1.upto.day"]);
    if (!isNaN(nen) && !isNaN(month) && !isNaN(day)) {
        let year = kanjidate.gengouToSeireki("令和", nen);
        let sqldate = kanjidate.toSqldate(year, month, day);
        return kanjidate.sqldateToKanji(sqldate, {padZero: true});
    }
    return null;
}


