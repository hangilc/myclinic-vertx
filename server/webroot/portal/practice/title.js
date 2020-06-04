import { Component } from "./component.js";

export class Title extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.textElement = map.text;
        this.menuElement = map.menu;
    }

    async init(visit, currentVisitId, tempVisitId) {
        this.textElement.text(this.rep(visit.visitedAt));
        if (currentVisitId === visit.visitId) {
            this.ele.addClass("current-visit");
        } else if (tempVisitId === visit.visitId) {
            this.ele.addClass("temp-visit");
        }
        this.menuElement.append(this.menuLink("この診察を削除", event => this.doDelete()));
        this.menuElement.append(this.menuLink("暫定診察に設定", event => this.doTempVisit()));
    }

    doDelete(){
        console.log("delete visit");
    }

    doTempVisit(){
        console.log("temp visit");
    }

    menuLink(name, cb){
        let a = $("<button>", {
            type: "button",
            class: "btn btn-link",
            href:"javascript:void(0)"
        });
        a.text(name);
        a.on("click", cb);
        return a;
    }

    createPopup(){
        let wrap = $("<div>");
        {
            let name = "popup";
            let link = $("<button>", {
                type: "button",
                class: "btn btn-link"
            });
            link.text(name);

            wrap.append(link);
        }
        return wrap;
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

    appendTo(element) {
        element.append(this.ele);
    }
}

