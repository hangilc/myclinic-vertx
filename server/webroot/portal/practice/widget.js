import {Component} from "../js/component.js";

export class Widget extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.widgetCloseElement = map.widgetClose;
    }

    init(){
        super.init();
        if( this.widgetCloseElement ){
            this.widgetCloseElement.on("click", event => this.close(null));
        }
        return this;
    }

    set(){
        super.set();
        return this;
    }

    onClose(cb){
        this.on("close", (event, result) => cb(result));
    }

    close(result){
        this.trigger("close", result);
    }

}