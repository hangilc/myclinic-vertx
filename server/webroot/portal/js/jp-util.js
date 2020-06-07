
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

function charToZenkaku(ch){
    if( ch in toZenkakuMap ){
        return toZenkakuMap[ch];
    } else {
        return ch;
    }
}

export function toZenkaku(str){
    if( typeof str !== "string" ){
        str = "" + str;
    }
    return str.split("").map(charToZenkaku).join("");
}