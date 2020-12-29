
export class HokenDisp {
    constructor(hokenRep){
        this.ele = document.createElement("div");
        this.ele.innerText = hokenRep;
        this.ele.addEventListener("click", event => this.ele.dispatchEvent(new Event("edit")));
    }
}