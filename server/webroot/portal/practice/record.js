import {Component} from "./component.js";

export class Record extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.titleElement = map.title;
        this.enterTextElement = map.left.enterText;
        this.textWrapperElement = map.left.textWrapper;
        this.rightElement = map.right_;
        this.hokenWrapperElement = map.right.hokenWrapper;
        this.shinryouMenuElement = map.right.shinryouMenu;
        this.shinryouWrapperElement = map.right.shinryouWrapper;
        this.shochiMenuElement = map.right.shochiMenu;
        this.shochiWrapperElement = map.right.shochiWrapper;
    }

    init(visitFull, hokenRep, titleFactory, textFactory, hokenFactory, shinryouFactory){
        let compTitle = titleFactory.create(visitFull.visit).appendTo(this.titleElement);
        visitFull.texts.forEach(text => {
            let compText = textFactory.create(text).appendTo(this.textWrapperElement);
        });
        let compHoken = hokenFactory.create(hokenRep).appendTo(this.hokenWrapperElement);
        visitFull.shinryouList.forEach(shinryouFull => {
            let compShinryou = shinryouFactory.create(shinryouFull)
                .appendTo(this.shinryouWrapperElement);
        })
    }
}