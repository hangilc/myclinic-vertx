import * as JpUtil from "./jp-util.js";

class Item {
    constructor() {
        this.lines = [];
    }

    addLine(line) {
        this.lines.push(line);
    }

    formatAsLines(indexPart) {
        let result = [];
        let lines = this.lines.slice();
        let pre = "　".repeat(indexPart.length);
        let tabPos = 21;
        let lineMax = 31;

        function isRightSize(str){
            let s = str.replace(/\s+$/, "");
            return s.length <= lineMax;
        }

        let iterMax = 20;
        while (lines.length > 0) {
            if( --iterMax <= 0 ){
                throw "Too many iteration";
            }
            let line = lines.shift();
            line = line.replace(/^\s+/, "");
            let lead = result.length === 0 ? indexPart : pre;
            let m = /^(.*?)(\S+)(錠|カプセル|ｇ|g)$/.exec(line);
            if (m) {
                let a = JpUtil.toZenkaku(m[1].trim());
                let gap = tabPos - lead.length - a.length;
                if( gap > 0 ){
                    let b = "　".repeat(gap);
                    let ll = `${lead}${a}${b}${m[2]}${m[3]}`;
                    if (isRightSize(ll)) {
                        result.push(ll);
                        continue;
                    }
                }
            } else if ((m = /^(.*?)(\S+)(日分(（実日数）)?|回分)$/.exec(line)) != null) {
                let a = JpUtil.toZenkaku(m[1].trim());
                let gap = tabPos - lead.length - a.length;
                if( gap > 0 ){
                    let b = "　".repeat(gap);
                    let ll = `${lead}${a}${b}${m[2]}${m[3]}`;
                    if (isRightSize(ll)) {
                        result.push(ll);
                        continue;
                    }
                }
            }
            let ll = `${lead}${line}`;
            if (!isRightSize(ll)) {
                let a = ll.substring(0, lineMax);
                let b = ll.substring(lineMax);
                result.push(a);
                lines.unshift(b);
                continue;
            }
            result.push(ll);
        }
        return result;
    }

}

class Presc {
    constructor() {
        this.prefix = [];
        this.items = [];
        this.controls = [];
    }

    addPrefix(text) {
        this.prefix.push(text);
    }

    addItem(item) {
        this.items.push(item);
    }

    addControl(text) {
        this.controls.push(text);
    }

    hasControl() {
        return this.controls.length > 0;
    }

    format() {
        let lines = [];
        lines = lines.concat(this.prefix);
        for (let index = 1; index <= this.items.length; index++) {
            let indexPart = JpUtil.toZenkaku(`${index}）`);
            lines = lines.concat(this.items[index - 1].formatAsLines(indexPart));
        }
        lines = lines.concat(this.controls);
        return lines.join("\n");
    }
}

let patIndex = /^([１２３４５６７８９０0-9]+)[）)]\s*(.*)/;

function parsePresc(src) {
    if (!src) {
        src = "";
    }
    let result = new Presc();
    let curItem = null;
    for (let line of splitToLines(src)) {
        let m = patIndex.exec(line);
        if (m) {
            let s = m[2];
            if (curItem) {
                result.addItem(curItem);
            }
            curItem = new Item();
            curItem.addLine(s);
        } else {
            if (line.startsWith("@") || result.hasControl()) {
                result.addControl(line);
            } else {
                if (!curItem) {
                    result.addPrefix(line);
                } else {
                    curItem.addLine(line);
                }
            }
        }
    }
    if (curItem) {
        result.addItem(curItem);
    }
    return result;
}

function splitToLines(text) {
    return text.split(/\r\n|\n|\r/g);
}

export function formatPresc(src) {
    let presc = parsePresc(src);
    return presc.format();
}
