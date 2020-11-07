import {parseElement} from "../js/parse-element.js";

let html = `
<h2>診断書作成</h2>

<div id="medcert-wrapper"></div>
`;

let tmpl = `
<div>
    <div>
        氏名 <input name="shimei">
    </div>
    <div>
        生年月日 <input name="birth-date">
    </div>
    <div>
        診断名 <input name="diagnosis">
    </div>
    <textarea rows="6" cols="40" name="text"></textarea>
    <div>
        発行日 <input name="issue-date">
    </div>
    <div>
        <button type="button" class="btn btn-primary x-create">作成</button>
    </div>
</div>
`;

export function getHtml() {
    return html;
}

class MedCert {
    constructor(rest) {
        this.rest = rest;
        this.ele = $(tmpl);
        let map = parseElement(this.ele);
        map.create.on("click", event => this.doCreate());
    }

    async doCreate() {
        let data = await this.collectData();
        let savePath = await this.rest.renderMedCert(data);
        alert("Saved to " + savePath);
    }

    async collectData() {
        let clinicInfo = await this.rest.getClinicInfo();
        return {
            patientName: this.inputValue("shimei"),
            birthDate: this.inputValue("birth-date"),
            diagnosis: this.inputValue("diagnosis"),
            text: this.ele.find("textarea[name=text]").val(),
            issueDate: this.inputValue("issue-date"),
            postalCode: clinicInfo.postalCode,
            address: clinicInfo.address,
            phone: "Tel: " + clinicInfo.tel,
            fax: "Fax: " + clinicInfo.fax,
            clinicName: clinicInfo.name,
            doctorName: clinicInfo.doctorName
        };
    }

    inputValue(name) {
        return this.ele.find("input[name=" + name + "]").val();
    }

}

export async function initMedcert() {
    let medCert = new MedCert(rest);
    $("#medcert-wrapper").append(medCert.ele);
}
