import {Component} from "./component.js";

export class Shinryou extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.textElement = map.text;
    }

    init(shinryouFull){
        this.textElement.text(shinryouFull.master.name);
    }

}