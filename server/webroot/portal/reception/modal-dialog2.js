import {parseElement} from "../js/parse-node.js";

let tmpl = `
<h3 class="x-title"></h3>
<div class="x-content"></div>
<div class="command-box x-commands"></div>
`;

export class ModalDialog2 {
    constructor(title){
        let ele = document.createElement("div");
        ele.innerHTML = tmpl;
        ele.classList.add("modal-dialog-content");
        ele.style.display = "block";
        this.ele = ele;
        let backdrop = document.createElement("div");
        backdrop.classList.add("modal-dialog-backdrop");
        this.backdrop = backdrop;
        let map = parseElement(ele);
        map.title.innerText = title;
        this.map = map;
    }

    getContent(){
        return this.map.content;
    }

    getCommands(){
        return this.map.commands;
    }

    close(retVal){
        this.backdrop.remove();
        this.ele.style.display = "none";
        this.ele.remove();
        this.ele.dispatchEvent(new CustomEvent("dialog-closed", { detail: retVal }));
    }

    open(){
        return new Promise(resolve => {
            document.body.append(this.backdrop);
            document.body.append(this.ele);
            this.ele.addEventListener("dialog-closed", event => resolve(event.detail));
        });
    }
}
