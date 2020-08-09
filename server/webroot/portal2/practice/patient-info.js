import {parseElement} from "../js/parse-element.js";

let html = `
[<span class="x-patient-id"></span>]
<span class="x-last-name"></span><span class="x-first-name"></span>
(<span class="x-last-name-yomi"></span><span class="x-first-name-yomi"></span>)
<span class="x-birthday"></span>生
(<span class="x-age"></span>才)
<span class="x-sex"></span>性
<a href="javascript:void(0)" class="x-detail-link">詳細</a>
<div class="x-detail-wrapper">
    <div class="detail">
        <div class="table-row">
            <div class="label">住所</div>
            <div class="x-address value"></div>
        </div>
        <div class="table-row">
            <div class="label">電話番号</div>
            <div class="x-phone value"></div>
        </div>
    </div>
</div>
`;

export function createPatientInfo(registerCallback) {
    let ele = document.createElement("div");
    ele.classList.add("patient-info");
    ele.innerHTML = html;
    let map = parseElement(ele);
    map.detailLink.onclick = event => {
        if (map.detailWrapper.style.display === "none") {
            map.detailWrapper.style.display = "block";
        } else {
            map.detailWrapper.style.display = "none";
        }
    };
    registerCallback(patient => {
        if (patient != null) {
            map.detailWrapper.style.display = "none";
            setPatient(map, patient);
            ele.style.display = "block";
        } else {
            ele.style.display = "none";
        }
    });
    return ele;
}

function setPatient(map, patient) {
    let birthday = new Date(patient.birthday);
    map.patientId.innerText = "" + patient.patientId;
    map.lastName.innerText = patient.lastName;
    map.firstName.innerText = patient.firstName;
    map.lastNameYomi.innerText = patient.lastNameYomi;
    map.firstNameYomi.innerText = patient.firstNameYomi;
    map.birthday.innerText = formatDate(birthday);
    map.age.innerText = "" + calcAge(birthday);
    map.sex.innerText = patient.sex === "M" ? "男" : "女";
    map.address.innerText = patient.address;
    map.phone.innerText = patient.phone;
}

function formatDate(date) {
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    return `${year}年${month}月${day}日`;
}

function calcAge(date) {
    let now = new Date();
    let age = now.getFullYear() - date.getFullYear();
    if (now.getMonth() > date.getMonth()) {
        return age;
    } else if (now.getMonth() < date.getMonth()) {
        return age - 1;
    } else {
        if (now.getDate() >= date.getDate()) {
            return age;
        } else {
            return age - 1;
        }
    }
}