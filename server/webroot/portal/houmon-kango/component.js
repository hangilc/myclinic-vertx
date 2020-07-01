export {parseElement} from "../js/parse-element.js";
import {parseElement} from "../js/parse-element.js";

let template = "<div></div>";

export class Component {
    constructor(ele) {
        if( !ele ){
            ele = $(template);
        }
        this.ele = ele;
    }

    getMap(){
        return parseElement(this.ele);
    }

    setClickHandler(ele, handler){
        ele.on("click", event => handler());
    }

    on(eventType, callback){
        if( !this.eventDispatcher ){
            this.eventDispatcher = {};
        }
        let fs = this.eventDispatcher[eventType];
        if( !fs ){
            fs = [callback];
            this.eventDispatcher[eventType] = fs;
        } else {
            fs.push(callback);
        }
    }

    trigger(eventType, arg){
        if( this.eventDispatcher ){
            let fs = this.eventDispatcher[eventType];
            if( fs ){
                for(let cb of fs){
                    cb(arg);
                }
            }
        }
    }

    convertToElement(element){
        if( element instanceof Component ){
            return element.ele;
        } else {
            return element;
        }
    }

    appendTo(element) {
        element = this.convertToElement(element);
        element.append(this.ele);
        return this;
    }

    prependTo(element){
        element = this.convertToElement(element);
        element.prepend(this.ele);
        return this;
    }

    remove() {
        this.ele.remove();
        return this;
    }

    detach(){
        this.ele.detach();
        return this;
    }

    putBefore(element) {
        element = this.convertToElement(element);
        element.before(this.ele);
        return this;
    }

    putAfter(element) {
        element = this.convertToElement(element);
        element.after(this.ele);
        return this;
    }

    replace(element) {
        element = this.convertToElement(element);
        element.after(this.ele);
        element.detach();
        return this;
    }
}
