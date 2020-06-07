import {Component} from "./component.js";

export class Record extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.ele.data("component", this);
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

    init(visitFull, hokenRep, titleFactory, textFactory, hokenFactory, shinryouFactory,
         textEnterFactory, shinryouRegularDialogFactory){
        //this.ele.attr("data-visit-id", visitFull.visit.visitId);
        this.visitFull = visitFull;
        this.textFactory = textFactory;
        this.shinryouFactory = shinryouFactory;
        this.titleComponent = titleFactory.create(visitFull.visit).appendTo(this.titleElement);
        visitFull.texts.forEach(text => {
            this.addText(text);
        });
        this.enterTextElement.on("click", event => {
            let comp = textEnterFactory.create(this.visitFull.visit.visitId);
            comp.onEntered((event, entered) => {
                comp.remove();
                textFactory.create(entered).appendTo(this.textWrapperElement);
            });
            comp.onCancel(event => comp.remove());
            comp.putBefore(this.enterTextElement);
        });
        let compHoken = hokenFactory.create(hokenRep).appendTo(this.hokenWrapperElement);
        this.shinryouMenuElement.on("click", async event => {
            let result = await shinryouRegularDialogFactory.create(visitFull.visit.visitId).open();
            if( result.mode === "entered" ){
                let shinryouIds = result.shinryouIds;
                let shinryouFullList = await this.rest.listShinryouFullByIds(shinryouIds);
                shinryouFullList.forEach(sf => this.addShinryou(sf, true));
            }
        });
        visitFull.shinryouList.forEach(shinryouFull => {
            this.addShinryou(shinryouFull, false);
        })
    }

    addShinryou(shinryouFull, searchLocation=true){
        let compShinryou = this.shinryouFactory.create(shinryouFull);
        if( searchLocation ){
            let shinryoucode = shinryouFull.shinryou.shinryoucode;
            let xs = this.shinryouWrapperElement.find(".practice-shinryou");
            let found = false;
            for(let i=0;i<xs.length;i++){
                let x = xs.slice(i, i+1);
                let c = x.data("component");
                let code = c.getShinryoucode();
                if( shinryoucode < code ){
                    compShinryou.putBefore(x);
                    found = true;
                    break;
                }
            }
            if( !found ){
                compShinryou.appendTo(this.shinryouWrapperElement);
            }
        } else {
            compShinryou.appendTo(this.shinryouWrapperElement);
        }
    }

    addText(text){
        this.textFactory.create(text).appendTo(this.textWrapperElement);
    }

    getVisitId(){
        return this.visitFull.visit.visitId;
    }

    markAsCurrent(){
        this.titleComponent.markAsCurrent();
    }

    markAsTemp(){
        this.titleComponent.markAsTemp();
    }

    clearMark(){
        this.titleComponent.clearMark();
    }

    onDeleted(cb){
        this.titleComponent.onDeleted(cb);
    }

    onTempVisit(cb){
        this.titleComponent.onTempVisit(cb);
    }

    onClearTempVisit(cb){
        this.titleComponent.onClearTempVisit(cb);
    }
}