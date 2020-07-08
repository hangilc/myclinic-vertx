import * as JpUtil from "./jp-util.js";

class NaifukuFirstLine {
    constructor(name, amount) {
        this.name = name;
        this.amount = amount;
    }

    format(indexPart){
        let gap = 21 - indexPart.length - this.name.length;
        let spc = gap > 0 ? "　".repeat(gap) : "　";
        return `${indexPart}${this.name}${spc}${this.amount}`;
    }
}

class OtherFirstLine {
    constructor(s) {
        this.s = s;
    }

    format(indexPart) {
        return `${indexPart}${this.s}`;
    }
}

class Item {
    constructor(firstLine) {
        this.firstLine = firstLine;
        this.lines = [];
    }

    addLine(line){
        this.lines.push(line);
    }

    formatAsLines(indexPart){
        let lines = [this.firstLine.format(indexPart)];
        lines = lines.concat(this.lines);
        return lines;
    }
}

class Presc {
    constructor() {
        this.prefix = [];
        this.items = [];
        this.controls = [];
    }

    addPrefix(text){
        this.prefix.push(text);
    }

    addItem(item){
        this.items.push(item);
    }

    addControl(text){
        this.controls.push(text);
    }

    hasControl(){
        return this.controls.length > 0;
    }

    format(){
        let lines = [];
        lines = lines.concat(this.prefix);
        for(let index = 1; index <= this.items.length; index++){
            let indexPart = JpUtil.toZenkaku(`${index}）`);
            lines = lines.concat(this.items[index-1].formatAsLines(indexPart));
        }
        lines = lines.concat(this.controls);
        return lines.join("\n");
    }
}

let patIndex = /^([１２３４５６７８９０0-9]+)[）)]\s*(.*)/;
let patNaifuku = /(\S+)\s+(.+(錠|カプセル|ｇ|g))/;

function parsePresc(src){
    if( !src ){
        src = "";
    }
    let result = new Presc();
    let curItem = null;
    for(let line of splitToLines(src)){
        let m = patIndex.exec(line);
        if( m ){
            let firstLineSrc = m[2];
            let mm = patNaifuku.exec(firstLineSrc);
            let firstLine = null;
            if( mm ){
                firstLine = new NaifukuFirstLine(mm[1], mm[2]);
            } else {
                firstLine = new OtherFirstLine(firstLineSrc);
            }
            if( curItem ){
                result.addItem(curItem);
            }
            curItem = new Item(firstLine);
        } else {
            if( line.startsWith("@") || result.hasControl() ) {
                result.addControl(line);
            } else {
                if( !curItem ){
                    result.addPrefix(line);
                } else {
                    curItem.addLine(line);
                }
            }
        }
    }
    if( curItem ){
        result.addItem(curItem);
    }
    return result;
}

function splitToLines(text){
    return text.split(/\r\n|\n|\r/g);
}

function indexRepToIndex(indexRep){
    return parseInt(JpUtil.toAscii(indexRep));
}

export function formatPresc(src){
    let presc = parsePresc(src);
    return presc.format();
}

