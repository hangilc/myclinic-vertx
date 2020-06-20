import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-element.js";

export class HokenSelectDialog extends Dialog {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.bodyElement = map.body;
        this.itemTemplateHtml = map.itemTemplate.html();
        this.enterElement = map.enter;
        this.cancelElement = map.cancel;
    }

    init(visitId){
        super.init();
        this.visitId = visitId;
        this.cancelElement.on("click", event => this.close());
        this.enterElement.on("click", event => this.doEnter());
        return this;
    }

    set(hokenEx, current){
        console.log("hoken", hokenEx, current);
        super.set();
        if( hokenEx.shahokokuho ){
            let shahokokuho = hokenEx.shahokokuho;
            let checked = current.shahokokuho && current.shahokokuho.shahokokuhoId === shahokokuho.shahokokuhoId;
            let item = this.createItemElement(shahokokuho.hokenRep, "shahokokuho", shahokokuho, checked);
            this.bodyElement.append(item);
        }
        if( hokenEx.koukikourei ){
            let koukikourei = hokenEx.koukikourei;
            let checked = current.koukikourei && current.koukikourei.koukikoureiId === koukikourei.koukikoureiId;
            let item = this.createItemElement(koukikourei.hokenRep, "koukikourei", koukikourei, checked);
            this.bodyElement.append(item);
        }
        if( hokenEx.roujin ){
            let roujin = hokenEx.roujin;
            let checked = current.roujin && current.roujin.roujinId === roujin.roujinId;
            let item = this.createItemElement(roujin.hokenRep, "roujin", roujin, checked);
            this.bodyElement.append(item);
        }
        let currentKouhiIds = [current.kouhi1, current.kouhi2, current.kouhi3].filter(kouhi => kouhi != null)
            .map(kouhi => kouhi.kouhiId);
        for(let kouhi of [hokenEx.kouhi1, hokenEx.kouhi2, hokenEx.kouhi3]){
            if( kouhi ){
                let checked = currentKouhiIds.includes(kouhi.kouhiId);
                let item = this.createItemElement(kouhi.hokenRep, "kouhi", kouhi, checked);
                this.bodyElement.append(item);
            }
        }
        return this;
    }

    onChanged(cb){
        this.on("changed", (event, hoken, hokenRep) => cb(hoken, hokenRep));
    }

    async doEnter(){
        if( !(this.visitId > 0) ){
            throw new Error("Invalid visitId");
        }
        let visit = {"visitId": this.visitId};
        let es = this.bodyElement.find("input[type=checkbox]:checked");
        let kouhiIndex = 1;
        for(let i=0;i<es.length;i++){
            let check = es.slice(i, i+1);
            let kind = check.data("kind");
            let data = check.data("data");
            switch(kind){
                case "shahokokuho": {
                    visit.shahokokuhoId = data.shahokokuhoId;
                    break;
                }
                case "koukikourei": {
                    visit.koukikoureiId = data.koukikoureiId;
                    break;
                }
                case "roujin": {
                    visit.roujinId = data.roujinId;
                    break;
                }
                case "kouhi": {
                    if( kouhiIndex >= 1 && kouhiIndex <= 3 ){
                        visit[`kouhi${kouhiIndex}Id`] = data.kouhiId;
                        kouhiIndex += 1;
                    }
                    break;
                }
                default: {
                    alert("Unkonw data kind: "+ kind);
                    break;
                }
            }
        }
        await this.rest.updateHoken(visit);
        let updatedHoken = await this.rest.getHoken(this.visitId);
        let updatedHokenRep = await this.rest.hokenRep(updatedHoken);
        this.trigger("changed", [updatedHoken, updatedHokenRep]);
        this.close();
    }

    createItemElement(label, kind, data, checked){
        let e = $(this.itemTemplateHtml);
        let m = parseElement(e);
        m.input.data("kind", kind);
        m.input.data("data", data);
        if( checked ){
            m.input.prop("checked", true);
        }
        m.label.text(label);
        return e;
    }

}