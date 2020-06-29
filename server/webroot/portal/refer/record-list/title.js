import {Component} from "../../js/component.js";
import {parseElement} from "../../js/parse-element.js";

let template = `
    <div></div>
`;

class Title extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
    }

    init(){

    }

    set(visit){
        this.ele.text(this.rep(visit.visitedAt.substring(0, 10)));
    }

    rep(sqldatetime) {
        let data = kanjidate.sqldatetimeToData(sqldatetime);
        let nen = (data.nen + "").padStart(2, "0");
        let month = (data.month + "").padStart(2, "0");
        let day = (data.day + "").padStart(2, "0");
        let hour = (data.hour + "").padStart(2, "0");
        let minute = (data.minute + "").padStart(2, "0");
        return `${data.gengou.name}${nen}年${month}月${day}日（${data.youbi}） ${hour}時${minute}分`;
    }
}

class TitleFactory {
    create(visit, rest){
        let ele = $(template);
        let map = parseElement(ele);
        let comp = new Title(ele, map, rest);
        comp.init();
        comp.set(visit);
        return comp;
    }
}

export let titleFactory = new TitleFactory();

