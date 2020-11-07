import {gensymId} from "../js/gensym-id.js";
import {createElementFrom} from "../js/create-element-from.js";
import {parseElement} from "../js/parse-node.js";
import {DateInput} from "./date-input.js";
import {success, error} from "../js/opt-result.js";

let tmpl = `
<form onsubmit="return false;">
    <div class="form-row">
        <div class="form-group col-auto">
            <label for="gensym-last-name">姓</label>
            <input id="gensym-last-name" class="form-control x-last-name"/>
        </div>
        <div class="form-group col-auto">
            <label for="gensym-first-name">名</label>
            <input id="gensym-first-name" class="form-control x-first-name"/>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group col-auto">
            <label for="gensym-last-name">姓（よみ）</label>
            <input id="gensym-last-name-yomi" class="form-control x-last-name-yomi"/>
        </div>
        <div class="form-group col-auto">
            <label for="gensym-first-name">名（よみ）</label>
            <input id="gensym-first-name-yomi" class="form-control x-first-name-yomi"/>
        </div>
    </div>
    <div class="form-group">
        <label for="gensym-birthday">生年月日</label>
        <div class="x-birthday"></div>
    </div>
    <div class="form-group x-sex">
        <label for="gensym-sex">性別</label>
        <div class="form-row pl-2">
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="sex" id="gensym-sex-male" value="M">
            <label class="form-check-label" for="gensym-sex-male">男</label>
        </div>
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="sex" id="gensym-sex-female" value="F" checked>
            <label class="form-check-label" for="gensym-sex-female">女</label>
        </div>
        </div>
    </div>
    <div class="form-group">
        <label for="gensym-address">住所</label>
        <input id="gensym-address" class="form-control x-address"/>
    </div>
    <div class="form-group">
        <label for="gensym-phone">電話</label>
        <input id="gensym-phone" class="form-control w-50 x-phone"/>
    </div>
</form>
`;

export class PatientForm {
    constructor(){
        let ele = createElementFrom(tmpl);
        gensymId(ele);
        let map = parseElement(ele);
        this.map = map;
        let birthdayInput = new DateInput();
        this.birthdayInput = birthdayInput;
        map.birthday.appendChild(birthdayInput.ele);
        this.ele = ele;
    }

    getLastName(){
        return this.map.lastName.value;
    }

    getFirstName(){
        return this.map.firstName.value;
    }

    getLastNameYomi(){
        return this.map.lastNameYomi.value;
    }

    getFirstNameYomi(){
        return this.map.firstNameYomi.value;
    }

    getBirthday(){
        return this.birthdayInput.get();
    }

    getSex(){
        return this.map.sex.querySelector("input[name='sex']:checked").value;
    }

    getAddress(){
        return this.map.address.value;
    }

    getPhone(){
        return this.map.phone.value;
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