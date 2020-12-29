import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";

let tmpl = `
    <div></div>
`;

export class Hoken {
    constructor(){
        this.ele = createElementFrom(tmpl);
    }
}

class HokenOrig extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(patientId, date, visitId, hokenDispFactory, hokenSelectDialogFactory){
        super.init();
        this.patientId = patientId;
        this.date = date;
        this.visitId = visitId;
        this.hokenDispFactory = hokenDispFactory;
        this.hokenSelectDialogFactory = hokenSelectDialogFactory;
    }

    set(hoken, hokenRep){
        super.set();
        this.hoken = hoken;
        let compDisp = this.hokenDispFactory.create(hokenRep);
        compDisp.ele.on("click", event => this.doDispClick());
        this.ele.html("");
        compDisp.appendTo(this.ele);
    }

    onChanged(cb){
        this.on("changed", (event, hoken, hokenRep) => cb(hoken, hokenRep));
    }

    async doDispClick(){
        let current = this.hoken;
        let hoken = await this.rest.listAvailableHoken(this.patientId, this.date);
        await this.extendHoken(hoken);
        let dialog = this.hokenSelectDialogFactory.create(hoken, this.visitId, current);
        dialog.onChanged((updatedHoken, updatedHokenRep) => {
            this.trigger("changed", [updatedHoken, updatedHokenRep]);
        });
        await dialog.open();
    }

    async extendHoken(hoken){
        if( hoken.shahokokuho ){
            hoken.shahokokuho.hokenRep = await this.rest.shahokokuhoRep(hoken.shahokokuho);
        }
        if( hoken.roujin ){
            hoken.roujin.hokenRep = await this.rest.roujinRep(hoken.roujin);
        }
        if( hoken.koukikourei ){
            hoken.koukikourei.hokenRep = await this.rest.koukikoureiRep(hoken.koukikourei);
        }
        if( hoken.kouhi1 ){
            hoken.kouhi1.hokenRep = await this.rest.kouhiRep(hoken.kouhi1);
        }
        if( hoken.kouhi2 ){
            hoken.kouhi2.hokenRep = await this.rest.kouhiRep(hoken.kouhi2);
        }
        if( hoken.kouhi3 ){
            hoken.kouhi3.hokenRep = await this.rest.kouhiRep(hoken.kouhi3);
        }
    }
}