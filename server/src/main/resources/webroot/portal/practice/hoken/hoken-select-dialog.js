import {Dialog as Dialog1} from "../dialog.js";
import {Dialog} from "../../../js/dialog2.js";
import {parseElement} from "../../../js/parse-node.js";
import {createElementFrom} from "../../../js/create-element-from.js";

let footerTmpl = `
    <button type="button" class="btn btn-primary x-enter">入力</button>
    <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
`;

export class HokenSelectDialog extends Dialog {
    constructor(availableHoken, current){
        super();
        this.items = [];
        this.setTitle("保険選択");
        this.addItems(availableHoken, current);
        this.getFooter().innerHTML = footerTmpl;
        let fmap = parseElement(this.getFooter());
        fmap.enter.addEventListener("click", async event => await this.doEnter());
        fmap.cancel.addEventListener("click", event => this.close(null));
    }

    doEnter() {
        this.close(this.getSelected());
    }

    addItems(hokenEx, current){
        let wrapper = this.getBody();
        if( hokenEx.shahokokuho ){
            let shahokokuho = hokenEx.shahokokuho;
            let checked = current.shahokokuho && current.shahokokuho.shahokokuhoId === shahokokuho.shahokokuhoId;
            let item = new Item(shahokokuho.hokenRep, checked, obj => obj.shahokokuhoId = shahokokuho.shahokokuhoId);
            this.items.push(item);
            wrapper.append(item.ele);
        }
        if( hokenEx.koukikourei ){
            let koukikourei = hokenEx.koukikourei;
            let checked = current.koukikourei && current.koukikourei.koukikoureiId === koukikourei.koukikoureiId;
            let item = new Item(koukikourei.hokenRep, checked, obj => obj.koukikoureiId = koukikourei.koukikoureiId);
            this.items.push(item);
            wrapper.append(item.ele);
        }
        if( hokenEx.roujin ){
            let roujin = hokenEx.roujin;
            let checked = current.roujin && current.roujin.roujinId === roujin.roujinId;
            let item = new Item(roujin.hokenRep, checked, obj => obj.roujinId = roujin.roujinId);
            this.items.push(item);
            wrapper.append(item.ele);
        }
        let currentKouhiIds = [current.kouhi1, current.kouhi2, current.kouhi3].filter(kouhi => kouhi != null)
            .map(kouhi => kouhi.kouhiId);
        for(let kouhi of [hokenEx.kouhi1, hokenEx.kouhi2, hokenEx.kouhi3]){
            if( kouhi ){
                let checked = currentKouhiIds.includes(kouhi.kouhiId);
                let item = new Item(kouhi.hokenRep, checked, obj => {
                    if( !obj.kouhi1Id ){
                        obj.kouhi1Id = kouhi.kouhiId;
                    } else if( !obj.kouhi2Id ){
                        obj.kouhi2Id = kouhi.kouhiId;
                    } else if( !obj.kouhi3Id ){
                        obj.kouhi3Id = kouhi.kouhiId;
                    }
                });
                this.items.push(item);
                wrapper.append(item.ele);
            }
        }
    }

    getSelected(){
        let obj = {
            shahokokuhoId: 0,
            koukikoureiId: 0,
            roujinId: 0,
            kouhi1Id: 0,
            kouhi2Id: 0,
            kouhi3Id: 0
        };
        this.items.forEach(item => {
            if( item.isChecked() ){
                item.assignTo(obj);
            }
        })
        return obj;
    }
}

let itemTmpl = `
    <div class="form-check">
        <input type="checkbox" class="form-check-input x-input">
        <div class="form-check-label x-label"></div>
    </div>
`;

class Item {
    constructor(label, checked, assign){
        this.assign = assign;
        this.ele = createElementFrom(itemTmpl);
        let map = this.map = parseElement(this.ele);
        map.label.innerText = label;
        if( checked ){
            map.input.checked = true;
        }
    }

    isChecked(){
        return this.map.input.checked;
    }

    assignTo(obj){
        this.assign(obj);
    }
}

class HokenSelectDialogOrig extends Dialog1 {
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