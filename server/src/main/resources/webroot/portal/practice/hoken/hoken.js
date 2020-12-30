import {createElementFrom} from "../../../js/create-element-from.js";
import {HokenDisp} from "./hoken-disp.js";
import {HokenSelectDialog} from "./hoken-select-dialog.js";

let tmpl = `
    <div></div>
`;

export class Hoken {
    constructor(rest, patientId, date, hoken){
        this.rest = rest;
        this.patientId = patientId;
        this.date = date;
        this.hoken = hoken;
        this.ele = createElementFrom(tmpl);
        this.setDisp(hoken.rep);
    }

    setDisp(hokenRep){
        let disp = new HokenDisp(hokenRep);
        this.ele.innerHTML = "";
        this.ele.append(disp.ele);
        disp.ele.addEventListener("edit", async event => {
            event.stopPropagation();
            let available = await this.rest.listAvailableHoken(this.patientId, this.date);
            await this.extendHoken(available);
            let dialog = new HokenSelectDialog(available, this.hoken);
            let modified = await dialog.open();
            if( modified ){
                this.ele.dispatchEvent(new CustomEvent("enter", {detail: modified}));
            } else {
                this.ele.innerHTML = "";
                this.ele.append(disp.ele);
            }
        });
    }

    async extendHoken(available) {
        if (available.shahokokuho) {
            available.shahokokuho.hokenRep = await this.rest.shahokokuhoRep(available.shahokokuho);
        }
        if (available.roujin) {
            available.roujin.hokenRep = await this.rest.roujinRep(available.roujin);
        }
        if (available.koukikourei) {
            available.koukikourei.hokenRep = await this.rest.koukikoureiRep(available.koukikourei);
        }
        if (available.kouhi1) {
            available.kouhi1.hokenRep = await this.rest.kouhiRep(available.kouhi1);
        }
        if (available.kouhi2) {
            available.kouhi2.hokenRep = await this.rest.kouhiRep(available.kouhi2);
        }
        if (available.kouhi3) {
            available.kouhi3.hokenRep = await this.rest.kouhiRep(available.kouhi3);
        }
    }
}

// class HokenOrig extends Component {
//     constructor(ele, map, rest) {
//         super(ele, map, rest);
//     }
//
//     init(patientId, date, visitId, hokenDispFactory, hokenSelectDialogFactory){
//         super.init();
//         this.patientId = patientId;
//         this.date = date;
//         this.visitId = visitId;
//         this.hokenDispFactory = hokenDispFactory;
//         this.hokenSelectDialogFactory = hokenSelectDialogFactory;
//     }
//
//     set(hoken, hokenRep){
//         super.set();
//         this.hoken = hoken;
//         let compDisp = this.hokenDispFactory.create(hokenRep);
//         compDisp.ele.on("click", event => this.doDispClick());
//         this.ele.html("");
//         compDisp.appendTo(this.ele);
//     }
//
//     onChanged(cb){
//         this.on("changed", (event, hoken, hokenRep) => cb(hoken, hokenRep));
//     }
//
//     async doDispClick(){
//         let current = this.hoken;
//         let hoken = await this.rest.listAvailableHoken(this.patientId, this.date);
//         await this.extendHoken(hoken);
//         let dialog = this.hokenSelectDialogFactory.create(hoken, this.visitId, current);
//         dialog.onChanged((updatedHoken, updatedHokenRep) => {
//             this.trigger("changed", [updatedHoken, updatedHokenRep]);
//         });
//         await dialog.open();
//     }
//
//     async extendHoken(hoken){
//         if( hoken.shahokokuho ){
//             hoken.shahokokuho.hokenRep = await this.rest.shahokokuhoRep(hoken.shahokokuho);
//         }
//         if( hoken.roujin ){
//             hoken.roujin.hokenRep = await this.rest.roujinRep(hoken.roujin);
//         }
//         if( hoken.koukikourei ){
//             hoken.koukikourei.hokenRep = await this.rest.koukikoureiRep(hoken.koukikourei);
//         }
//         if( hoken.kouhi1 ){
//             hoken.kouhi1.hokenRep = await this.rest.kouhiRep(hoken.kouhi1);
//         }
//         if( hoken.kouhi2 ){
//             hoken.kouhi2.hokenRep = await this.rest.kouhiRep(hoken.kouhi2);
//         }
//         if( hoken.kouhi3 ){
//             hoken.kouhi3.hokenRep = await this.rest.kouhiRep(hoken.kouhi3);
//         }
//     }
// }