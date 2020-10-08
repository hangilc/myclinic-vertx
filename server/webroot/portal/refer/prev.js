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
        if( data["doctorName"] ){
            data["refer-hospital"] = data.referHospital;
            data["refer-doctor"] = data.referDoctor;
            data["patient-name"] = data.patientName;
            data["patient-info"] = data.patientInfo;
            data["diagnosis"] = adjustDiagnosis(data.diagnosis);
            data["issue-date"] = data.issueDate;
            data["address-1"] = data.clinicPostalCode;
            data["address-2"] = data.clinicAddress;
            data["address-3"] = data.clinicPhone;
            data["address-4"] = data.clinicFax;
            data["clinic-name"] = data.clinicName;
            data["doctgr-name"] = data.doctorName;
        }
        this.trigger("copy", data);

        function adjustDiagnosis(diagnosis){
            if( diagnosis ){
                return diagnosis.replace(/^診断[:：]\s*/, "");
            } else {
                return "";
            }
        }
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
