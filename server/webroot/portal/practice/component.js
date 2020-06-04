export class Component {
    constructor(ele, map, rest) {
        this.ele = ele;
        this.map = map;
        this.rest = rest;
    }

    appendTo(element) {
        element.append(this.ele);
    }

    remove() {
        this.ele.remove();
    }

    putBefore(element) {
        element.before(this.ele);
    }

    replace(element) {
        element.after(this.ele);
        element.detach();
    }
}
