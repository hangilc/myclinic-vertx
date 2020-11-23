import {parseElement} from "../js/parse-node.js";
import {createElementFrom} from "../js/create-element-from.js";
import {HotlineController} from "./hotline-controller.js";

let tmpl = `
    <form class="x-hotline-form" onsubmit="return false;">
        <div class="mb-2">
            <textarea rows="3" class="form-control mb-2 x-hotline-input"
                style="font-size:80%"></textarea>
            <button class="btn btn-primary btn-sm" type="submit">送信</button>
            <button class="btn btn-secondary btn-sm x-roger">了解</button>
            <button class="btn btn-secondary btn-sm x-beep">Beep</button>
            <div class="dropdown d-inline-block x-freqs-menu">
                <button class="btn btn-link btn-sm dropdown-toggle x-hotlinie-freqs"
                        data-toggle="dropdown">常用
                </button>
                <div class="dropdown-menu x-freq-items"></div>
            </div>
            <div class="dropdown d-inline-block x-patients-menu">
                <button class="btn btn-link btn-sm dropdown-toggle x-hotlinie-patients"
                        data-toggle="dropdown">患者
                </button>
                <div class="dropdown-menu x-patient-items"></div>
            </div>
        </div>
    </form>
    <div class="border border-secondary rounded p-2 x-hotline-list"
         style="max-height: 20em; overflow-y: auto; font-size:80%"></div>
`;

let dropdownItemTmpl = `
<a class="dropdown-item" href="javascript:void(0)"></a>
`;

export class HotlineComponent {
    constructor(wrapper, sender, recipient, freqs, rest, printAPI){
        wrapper.innerHTML = tmpl;
        this.ele = wrapper;
        let map = this.map = parseElement(wrapper);
        freqs.forEach(f => this.addFreq(f));
        this.controller = new HotlineController(sender, recipient,
            map.hotlineList, rest, printAPI);
        this.rest = rest;
    }

    async init(){
        let controller = this.controller;
        await controller.init();
        this.map.hotlineForm.addEventListener("submit", async event => {
            let m = this.map.hotlineInput.value.trim();
            if (m !== "") {
                await controller.submit(m);
            }
            this.map.hotlineInput.value = "";
        });
        this.map.roger.addEventListener("click", async event => {
            event.preventDefault();
            await controller.submit("了解");
        });
        this.map.beep.addEventListener("click", async event => {
            event.preventDefault();
            await controller.sendBeep();
        });
        this.ele.addEventListener("click", event => {
            let target = event.target;
            if( target.classList.contains("dropdown-item") ){
                let msg = target.innerText;
                this.insertIntoTextInput(msg);
                this.map.hotlineInput.focus();
            }
        });
        $(this.map.patientsMenu).on("show.bs.dropdown", async event => {
            let wqList = await this.rest.listWqueueFull();
            let itemsWrapper = this.map.patientItems;
            itemsWrapper.innerHTML = "";
            for(let wq of wqList){
                let patient = wq.patient;
                let label = `${patient.lastName}${patient.firstName}(${patient.patientId})様、`;
                let a = createElementFrom(dropdownItemTmpl);
                a.innerText = label;
                itemsWrapper.append(a);
            }
        });
    }

    addFreq(text){
        let e = createElementFrom(dropdownItemTmpl);
        e.innerText = text;
        this.map.freqItems.append(e);
    }

    insertIntoTextInput(s){
        let ta = this.map.hotlineInput;
        let value = ta.value;
        let left = value.substring(0, ta.selectionStart);
        let right = value.substring(ta.selectionEnd, value.length);
        ta.value = left + s + right;
    }

}