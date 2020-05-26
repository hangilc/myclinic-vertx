import * as kanjidate from "./kanjidate.js";

let html = `
<div class="card">
    <div class="card-header"></div>
    <div class="card-body">
        <div class="row">
            <div class="col-sm-3">患者番号</div>
            <div class="col-sm-9 disp-patient-patient-id"></div>
            <div class="col-sm-3">氏名</div>
            <div class="col-sm-9 disp-patient-name"></div>
            <div class="col-sm-3">よみ</div>
            <div class="col-sm-9 disp-patient-yomi"></div>
            <div class="col-sm-3">生年月日</div>
            <div class="col-sm-9 disp-patient-birthday"></div>
            <div class="col-sm-3">性別</div>
            <div class="col-sm-9 disp-patient-sex"></div>
            <div class="col-sm-3">住所</div>
            <div class="col-sm-9 disp-patient-address"></div>
            <div class="col-sm-3">電話</div>
            <div class="col-sm-9 disp-patient-phone"></div>
        </div>
    </div>
</div>
</div>
`;

function birthdayRep(birthday){
    let rep = kanjidate.sqldateToKanji(birthday);
    let age = moment().diff(moment(birthday), "years");
    return `${rep}生　${age}才`;
}

function sexRep(sex){
    if( sex === "M" ){
        return "男";
    } else {
        return "女";
    }
}

export function disp(patient){
    let disp = $(html);
    disp.find(".card-header").text(patient.lastName + " " + patient.firstName);
    disp.find(".disp-patient-patient-id").text(patient.patientId);
    disp.find(".disp-patient-name").text(`${patient.lastName} ${patient.firstName}`);
    disp.find(".disp-patient-yomi").text(`${patient.lastNameYomi} ${patient.firstNameYomi}`);
    disp.find(".disp-patient-birthday").text(birthdayRep(patient.birthday));
    disp.find(".disp-patient-sex").text(sexRep(patient.sex));
    disp.find(".disp-patient-address").text(patient.address);
    disp.find(".disp-patient-phone").text(patient.phone);
    return disp;
}
