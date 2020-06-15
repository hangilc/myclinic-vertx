import {Component} from "../js/component.js";

export class Widget extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.titleElement = map.title;
        this.bodyElement = map.body;
        this.closeElement = map.close;
    }

    init(title, bodyComponent){
        this.titleElement.text(title);
        if( bodyComponent ){
            bodyComponent.appendTo(this.bodyElement);
        }
        if( this.closeElement ){
            this.closeElement.on("click", event => this.close());
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
        if( this.onCloseCallback ){
            let response = this.onCloseCallback();
            if( response === false ){
                return;
            }
        }
        this.ele.remove();
    }

}