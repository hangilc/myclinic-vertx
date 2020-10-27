function hyphenToCamel(s) {
    let ps = s.split(/-+/);
    for (let i = 1; i < ps.length; i++) {
        let p = ps[i];
        ps[i] = p[0].toUpperCase() + p.substring(1);
    }
    return ps.join("");
}

function probeXClass(e) {
    for (let cls of e.classList) {
        if (cls.startsWith("x-")) {
            e.classList.remove(cls);
            return hyphenToCamel(cls.substring(2));
        }
    }
    return null;
}

function parseElementIter(ele, map) {
    let name = probeXClass(ele);
    if (name) {
        if (name.endsWith("_")) {
            if( name.endsWith("__") ){
                name = name.substring(0, name.length - 2);
                map[name] = ele;
            } else {
                map[name] = ele;
                name = name.substring(0, name.length - 1);
                let submap = {};
                for (let child of ele.children) {
                    parseElementIter(child, submap);
                }
                map[name] = submap;
                return;
            }
        } else {
            map[name] = ele;
        }
    }
    for(let child of ele.children){
        parseElementIter(child, map);
    }
}

export function parseElement(ele) {
    let map = {};
    parseElementIter(ele, map);
    return map;
}

