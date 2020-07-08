
const toZenkakuMap = {
    "0": "０",
    "1": "１",
    "2": "２",
    "3": "３",
    "4": "４",
    "5": "５",
    "6": "６",
    "7": "７",
    "8": "８",
    "9": "９",
    ".": "．",
    " ": "　",
    "-": "ー"
};

const toCharMap = {};
for(let key in toZenkakuMap){
    toCharMap[toZenkakuMap[key]] = key;
}

function charToZenkaku(ch){
    if( ch in toZenkakuMap ){
        return toZenkakuMap[ch];
    } else {
        return ch;
    }
}

function zenkakuToChar(zn){
    if( zn in toCharMap ){
        return toCharMap[zn];
    } else {
        return zn;
    }
}

export function toZenkaku(str){
    if( typeof str !== "string" ){
        str = "" + str;
    }
    return str.split("").map(charToZenkaku).join("");
}

export function toAscii(str){
    if( typeof str !== "string" ){
        str = "" + str;
    }
    return str.split("").map(zenkakuToChar).join("");
}