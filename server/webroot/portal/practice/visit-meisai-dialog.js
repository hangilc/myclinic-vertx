import {Dialog} from "./dialog.js";
import {parseElement} from "../js/parse-element.js";

export class VisitMeisaiDialog extends Dialog {
    constructor(ele, map, rest){
        super(ele, map, rest);
        this.sectionsElement = map.sections;
        this.totalTenElement = map.totalTen;
        this.closeElement = map.close;
        this.itemTemplateHtml = map.itemTemplate.html();
        this.detailTemplateHtml = map.detailTemplate.html();
    }

    init(){
        super.init();
        this.closeElement.on("click", event => this.close());
        return this;
    }

    set(meisai){
        super.set();
        for(let sect of meisai.sections){
            let e = this.createSectionElement(sect);
            this.sectionsElement.append(e);
        }
        this.totalTenElement.text(meisai.totalTen);
        return this;
    }

    createSectionElement(section){
        let e = $(this.itemTemplateHtml);
        let map = parseElement(e);
        map.title.text(section.label);
        for(let item of section.items){
            let d = $(this.detailTemplateHtml);
            let dm = parseElement(d);
            dm.detailLabel.text(item.label);
            let ten = `${item.tanka}x${item.count}=${item.tanka * item.count}`;
            dm.detailTen.text(ten);
            map.detail.append(d);
        }
        return e;
    }
}