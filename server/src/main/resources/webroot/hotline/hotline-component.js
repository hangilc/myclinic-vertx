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
                <div class="dropdown-menu x-freqs-items"></div>
            </div>
        </div>
    </form>
    <div class="border border-secondary rounded p-2 x-hotline-list"
         style="max-height: 20em; overflow-y: auto; font-size:80%"></div>
`;

let freqTmpl = `
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
            await controller.submit("了解");
        });
        this.map.beep.addEventListener("click", async event => {
            await controller.sendBeep();
        });
        this.map.freqsMenu.querySelectorAll(".dropdown-item").forEach(e => {
            e.addEventListener("click", async event => {
                let text = event.target.innerText;
                await controller.submit(text);
            });
        });
    }

    addFreq(text){
        let e = createElementFrom(freqTmpl);
        e.innerText = text;
        this.map.freqsItems.append(e);
    }
}