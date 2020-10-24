import {Component} from "../js/component.js";

export class Widget extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.widgetCloseElement = map.widgetClose;
    }

    init(){
        if( this.widgetCloseElement ){
            this.widgetCloseElement.on("click", event => this.close());
        }
        return this;
    }

    set(){
        return this;
    }

    onClose(cb){
        this.onCloseCallback = cb;
    }

    close(){
        console.log("close");
        if( this.onCloseCallback ){
            let response = this.onCloseCallback();
            if( response === false ){
                return;
            }
        }
        this.ele.remove();
    }

}