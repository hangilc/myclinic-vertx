import {gensymId} from "../js/gensym-id.js";
import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";
import {success, error} from "../js/opt-result.js";

let tmpl = `
<form onsubmit="return false;">
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">氏名</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="form-control x-last-name mr-3"/>
            <input type="text" class="form-control x-first-name"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">よみ</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="form-control x-last-name-yomi mr-3"/>
            <input type="text" class="form-control x-first-name-yomi"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">生年月日</div>
        <div class="col-sm-10 form-inline x-birthday"></div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">性別</div>
        <div class="col-sm-10 form-inline">
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="sex" id="gensym-sex-male" value="M">
                <label class="form-check-label" for="gensym-sex-male">男</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="sex" id="gensym-sex-female" value="F" 
                    checked>
                <label class="form-check-label" for="gensym-sex-female">女</label>
        </div>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">住所</div>
        <div class="col-sm-10">
            <input type="text" class="form-control x-address"/>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label d-flex justify-content-end">電話</div>
        <div class="col-sm-10">
            <input type="text" class="form-control x-phone"/>
        </div>
    </div>
</form>
`;

export class PatientForm {
    constructor(patient){
        let ele = createElementFrom(tmpl);
        gensymId(ele);
        let map = parseElement(ele);
        this.map = map;
        let birthdayInput = new DateInput();
        this.birthdayInput = birthdayInput;
        map.birthday.appendChild(birthdayInput.ele);
        this.ele = ele;
        this.patientId = 0;
        if( patient ){
            this.set(patient);
        }
    }

    getLastName(){
        return this.map.lastName.value;
    }

    setLastName(value){
        this.map.lastName.value = value;
    }

    getFirstName(){
        return this.map.firstName.value;
    }

    setFirstName(value){
        this.map.firstName.value = value;
    }

    getLastNameYomi(){
        return this.map.lastNameYomi.value;
    }

    setLastNameYomi(value){
        this.map.lastNameYomi.value = value;
    }

    getFirstNameYomi(){
        return this.map.firstNameYomi.value;
    }

    setFirstNameYomi(value){
        this.map.firstNameYomi.value = value;
    }

    getBirthday(){
        return this.birthdayInput.get();
    }

    setBirthday(value){
        this.birthdayInput.set(value);
    }

    getSex(){
        return this.map.sex.querySelector("input[name='sex']:checked").value;
    }

    setSex(value){
        this.map.sex.querySelector(`input[value='${value}']`).checked = true;
    }

    getAddress(){
        return this.map.address.value;
    }

    setAddress(value){
        this.map.address.value = value;
    }

    getPhone(){
        return this.map.phone.value;
    }

    setPhone(value){
        this.map.phone.value = value;
    }

    set(patient){
        this.patientId = patient.patientId;
        this.setLastName(patient.lastName);
        this.setFirstName(patient.firstName);
        this.setLastNameYomi(patient.lastNameYomi);
        this.setFirstNameYomi(patient.firstNameYomi);
        this.setBirthday(patient.birthday);
        this.setSex(patient.sex);
        this.setAddress(patient.address);
        this.setPhone(patient.phone);
    }

    get(){
        let patientId = this.patientId || 0;
        let lastName = this.getLastName();
        if( lastName === "" ){
            return error("姓が入力されていません。");
        }
        let firstName = this.getFirstName();
        if( firstName === "" ){
            return error("名が入力されていません。");
        }
        let lastNameYomi = this.getLastNameYomi();
        if( lastNameYomi === "" ){
            return error("姓のよみが入力されていません。");
        }
        let firstNameYomi = this.getFirstNameYomi();
        if( firstNameYomi === "" ){
            return error("名のよみが入力されていません。");
        }
        let birthdayOpt = this.getBirthday();
        if( !birthdayOpt.ok ){
            return error("生年月日の入力が不適切です。：" + birthdayOpt.message);
        }
        let sex = this.getSex();
        if( !sex ){
            return error("性の入力が不適切です。");
        }
        let address = this.getAddress();
        let phone = this.getPhone();
        return success({
            patientId: patientId,
            lastName: lastName,
            firstName: firstName,
            lastNameYomi: lastNameYomi,
            firstNameYomi: firstNameYomi,
            birthday: birthdayOpt.value,
            sex: sex,
            address: address,
            phone: phone
        });
    }

}