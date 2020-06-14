export class Component {
    constructor(ele, map, rest) {
        this.ele = ele;
        this.map = map;
        this.rest = rest;
        //his.eventDispatcher = $("<div>");
    }

    on(eventType, callback){
        if( !this.eventDispatcher ){
            this.eventDispatcher = $("<div>");
        }
        this.eventDispatcher.on(eventType, callback);
    }

    trigger(eventType, arg){
        if( !this.eventDispatcher ){
            this.eventDispatcher = $("<div>");
        }
        this.eventDispatcher.trigger(eventType, arg);
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

    remove() {
        this.ele.remove();
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
