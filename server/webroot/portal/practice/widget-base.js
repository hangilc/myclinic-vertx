import {Component} from "./component.js";
import {parseElement} from "../js/parse-element.js";

let template = `
    <div class="mb-3 border border-secondary rounded p-3">
        <div class="d-flex p-2 mb-2" style="background-color: #ccc;">
            <div class="font-weight-bold flex-grow-1 x-widget-title"></div>
            <div><span class="font-weight-bold x-widget-close"
                       style="cursor: pointer;">&times;</span></div>
        </div>
        <div class="x-widget-body"></div>
    </div>
`;

export class WidgetBase extends Component {
    constructor(rest){
        let ele = $(template);
        let map = parseElement(ele);
        super(ele, map, rest);
        this.widgetCloseElement = map.widgetClose;
        this.widgetTitleElement = map.widgetTitle;
        this.widgetBodyElement = map.widgetBody;
    }

    init(title){
        super.init();
        this.widgetTitleElement.text(title);
        this.widgetCloseElement.on("click", event => this.close(null));
        return this;
    }

    set(){
        super.set();
        return this;
    }

    addToBody(element){
        this.widgetBodyElement.append(element);
    }

    onClose(cb){
        this.on("close", (event, result) => cb(result));
    }

    close(result){
        this.trigger("close", result);
        this.remove();
    }
}
