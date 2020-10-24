import {parseElement} from "../js/parse-element.js";
import {DateInput} from "./date-input.js";

let html = `
    <div class="row">
        <div class="col-sm-2 d-flex justify-content-end">患者番号</div>
        <div class="x-patient-id col-md-10"></div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end">氏名</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="x-last-name form-control"/>
            <input type="text" class="x-first-name form-control ml-2"/>
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end mt-2">よみ</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="x-last-name-yomi form-control"/>
            <input type="text" class="x-first-name-yomi form-control ml-2"/>
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end mt-2">生年月日</div>
        <div class="col-sm-10 form-inline x-birthday-">
            <select class="x-gengou form-control">
                <option>令和</option>
                <option>平成</option>
                <option selected>昭和</option>
                <option>大正</option>
                <option>明治</option>
            </select>
            <input type="text" class="x-nen form-control ml-2" size="3"/> 年
            <input type="text" class="x-month form-control ml-2" size="3"/> 月
            <input type="text" class="x-day form-control ml-2" size="3"/> 日
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end mt-2">性別</div>
        <div class="col-sm-10">
            <form class="form-inline x-sex" onsubmit="return false">
                <input type="radio" name="sex" value="M"> 男
                <input type="radio" name="sex" value="F" checked class="ml-2"> 女
            </form>
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end mt-2">住所</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="x-address form-control" size="50"/>
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-sm-2 d-flex justify-content-end mt-2">電話番号</div>
        <div class="col-sm-10 form-inline">
            <input type="text" class="x-phone form-control"/>
        </div>

    </div>
`;

export class PatientForm {
    constructor(ele) {
        if( !ele ){
            ele = $("<div>");
        } else {
            ele = $(ele);
        }
        if( ele.html().trim() === "" ){
            ele.html(html);
        }
        this.ele = ele;
        let map = parseElement(ele);
        this.map = map
        this.patientIdElement = map.patientId;
        this.lastNameElement = map.lastName;
        this.firstNameElement = map.firstName;
        this.lastNameYomiElement = map.lastNameYomi;
        this.firstNameYomiElement = map.firstNameYomi;
        this.birthdayElement = new DateInput(map.birthday);
        this.sexElement = map.sex;
        this.addressElement = map.address;
        this.phoneElement = map.phone;
        this.error = null;
    }

    init(){

    }

    set(patient){
        if( patient ){
            this.patientId = patient.patientId;
            if( this.patientIdElement ){
                this.patientIdElement.text(patient.patientId);
            }
            this.lastNameElement.val(patient.lastName);
            this.firstNameElement.val(patient.firstName);
            this.lastNameYomiElement.val(patient.lastNameYomi);
            this.firstNameYomiElement.val(patient.firstNameYomi);
            this.birthdayElement.val(patient.birthday);
            this.sexElement.val(patient.sex);
            this.addressElement.val(patient.address);
            this.phoneElement.val(patient.phone);
        } else {
            if( this.patientIdElement ){
                this.patientIdElement.text(null);
            }
            this.lastNameElement.val(null);
            this.firstNameElement.val(null);
            this.lastNameYomiElement.val(null);
            this.firstNameYomiElement.val(null);
            this.birthdayElement.val(null);
            this.sexElement.val(null);
            this.addressElement.val(null);
            this.phoneElement.val(null);
        }
    }

    getError(){
        let err = this.error;
        this.error = null;
        return err;
    }

    get(){
        let patientId = this.patientId || 0;
        let lastName = this.lastNameElement.val();
        if( lastName === "" ){
            this.error = "姓が入力されていません。";
            return undefined;
        }
        let firstName = this.firstNameElement.val();
        if( firstName === "" ){
            this.error = "名が入力されていません。";
            return undefined;
        }
        let lastNameYomi = this.lastNameYomiElement.val();
        if( lastNameYomi === "" ){
            this.error = "姓のよみが入力されていません。";
            return undefined;
        }
        let firstNameYomi = this.firstNameYomiElement.val();
        if( firstNameYomi === "" ){
            this.error = "名のよみが入力されていません。";
            return undefined;
        }
        let birthday = this.birthdayElement.val();
        if( !birthday ){
            this.error = "生年月日の入力が不適切です。";
            return undefined;
        }
        let sex = this.sexElement.val();
        if( !sex ){
            this.error = "性の入力が不適切です。";
            return undefined;
        }
        let address = this.addressElement.val();
        let phone = this.phoneElement.val();
        return {
            patientId: patientId,
            lastName: lastName,
            firstName: firstName,
            lastNameYomi: lastNameYomi,
            firstNameYomi: firstNameYomi,
            birthday: birthday,
            sex: sex,
            address: address,
            phone: phone
        };
    }

}