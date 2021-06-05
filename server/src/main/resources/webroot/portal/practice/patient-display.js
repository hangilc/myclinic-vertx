import * as kanjidate from "../../js/kanjidate.js";
import {createElementFrom} from "../../js/create-element-from.js";
import {parseElement} from "../../js/parse-node.js";
import {CalloutDialog} from "../../js/phone.js";
import {detectPhoneNumber} from "../../js/phone-number.js";
import {callout} from "../../js/phone.js";

let tmpl = `
    <div>
        [<span class="x-patient-id"></span>]
        <span class="x-last-name"></span><span class="x-first-name"></span>
        (<span class="x-last-name-yomi"></span><span class="x-first-name-yomi"></span>)
        <span class="x-birthday"></span>生
        (<span class="x-age"></span>)
        <span class="x-sex"></span>
        <a href="javascript:void(0)" class="x-detail-link">詳細</a>
        <div class="x-detail d-none row">
            <div class="col-sm-3">住所</div>
            <div class="x-address col-sm-9"></div>
            <div class="col-sm-3">電話番号</div>
            <div class="x-phone col-sm-9"></div>
        </div>
    </div>
`;

export class PatientDisplay {
    constructor(patient, rest){
        this.ele = createElementFrom(tmpl);
        this.rest = rest;
        let map = parseElement(this.ele);
        map.patientId.innerText = patient.patientId;
        map.lastName.innerText = patient.lastName;
        map.firstName.innerText = patient.firstName;
        map.lastNameYomi.innerText = patient.lastNameYomi;
        map.firstNameYomi.innerText = patient.firstNameYomi;
        map.birthday.innerText = kanjidate.sqldateToKanji(patient.birthday);
        map.age.innerText = kanjidate.calcAge(patient.birthday) + "才";
        map.sex.innerText = patient.sex === "M" ? "男性" : "女性";
        map.address.innerText = patient.address;
        this.setupPhone(map.phone, patient.phone);
        map.detailLink.addEventListener("click", event => {
            if( map.detail.classList.contains("d-none") ){
                map.detail.classList.remove("d-none");
            } else {
                map.detail.classList.add("d-none");
            }
        });
    }

    setupPhone(wrapper, phone){
        wrapper.innerHTML = "";
        const spanTmpl = `<span class="x-span mr-2"></span>`;
        const buttonTmpl = `<button class="btn btn-sm mr-2">発信</button>`;
        if( phone) {
            const nums = detectPhoneNumber(phone);
            if( nums && nums.length > 0 ){
                let left = 0;
                for(let m of nums){
                    let s = phone.substring(left, m.index);
                    wrapper.appendChild(document.createTextNode(s));
                    const span = createElementFrom(spanTmpl);
                    parseElement(span).span.innerText = phone.substring(m.index, m.index + m.length);
                    wrapper.appendChild(span);
                    const button = createElementFrom(buttonTmpl);
                    button.addEventListener("click", async e => {
                        const phoneNumber = m.phone;
                        await callout(phoneNumber);
                    });
                    wrapper.appendChild(button);
                }
            } else {
                wrapper.innerText = phone;
            }
        }
    }

    async doPhone(){
        const dialog = new CalloutDialog("", this.rest);
        dialog.open();
    }
}
