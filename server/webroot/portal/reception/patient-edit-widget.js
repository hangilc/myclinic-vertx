import {Component} from "../js/component.js";
import {PatientDisp} from "./patient-disp.js";
import {parseElement} from "../js/parse-element.js";
import {compareBy} from "../js/general-util.js";
import * as kanjidate from "../js/kanjidate.js";

let tableRowHtml = `
<tr>
    <td class="x-rep"></td>
    <td class="x-valid-from"></td>
    <td class="x-valid-upto"></td>
    <td class="x-honnin"></td>
    <td class="x-manip">
        <a href="javascript:void(0)" class="x-edit">編集</a>
        <a href="javascript:void(0)" class="x-delete">削除</a>
    </td>
</tr>
`;

export class PatientEditWidget extends Component {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.disp = new PatientDisp(map.disp_, map.disp, rest);
        this.hokenListElement = map.hokenList;
    }

    init(){
        this.disp.init();
        this.setupDispConverters(this.disp);
        return this;
    }

    set(patient, currentHokenList){
        console.log("hoken", currentHokenList);
        this.disp.set(patient);
        this.setHokenList(currentHokenList);
        return this;
    }

    createTableRow(rep, validFrom, validUpto, honninKazoku, editFun, deleteFun){
        let ele = $(tableRowHtml);
        let map = parseElement(ele);
        map.rep.text(rep);
        map.validFrom.text(validFrom);
        map.validUpto.text(validUpto);
        map.honnin.text(honninKazoku);
        map.edit.on("click", event => editFun());
        map.delete.on("click", event => deleteFun());
        return ele;
    }

    setHokenList(hokenList){
        let tbody = this.hokenListElement.find("tbody").html("");
        let cmp = compareBy("-validFrom");
        hokenList.shahokokuhoList.sort(cmp);
        for(let shahokokuho of hokenList.shahokokuhoList){
            let tr = this.createTableRow(shahokokuho.rep, formatDate(shahokokuho.validFrom),
                formatDate(shahokokuho.validUpto), honninToKanji(shahokokuho.honnin),
                () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.koukikoureiList.sort(cmp);
        for(let koukikourei of hokenList.koukikoureiList){
            let tr = this.createTableRow(koukikourei.rep, formatDate(koukikourei.validFrom),
                formatDate(koukikourei.validUpto), "",
                () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.roujinList.sort(cmp);
        for(let roujin of hokenList.roujinList){
            let tr = this.createTableRow(roujin.rep, formatDate(roujin.validFrom),
                formatDate(roujin.validUpto), "",
                () => {}, () => {});
            tbody.append(tr);
        }
        hokenList.kouhiList.sort(cmp);
        for(let kouhi of hokenList.kouhiList){
            let tr = this.createTableRow(kouhi.rep, formatDate(kouhi.validFrom),
                formatDate(roujin.validUpto), "",
                () => {}, () => {});
            tbody.append(tr);
        }
    }

    setupDispConverters(disp){
        disp.setBirthdayConv(birthday => this.disp.birthdayAsKanji(birthday, {
            suffix: "生"
        }) + " " + this.disp.calcAge(birthday) + "才");
        disp.setSexConv(sex => this.disp.sexAsKanji(sex));
    }

}

function honninToKanji(honnin){
    return honnin ? "本人" : "家族";
}

function formatDate(sqldate){
    if( !sqldate || sqldate === "0000-00-00" ){
        return "";
    } else {
        return kanjidate.sqldateToKanji(sqldate, {padZero: true});
    }
}
