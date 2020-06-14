let sexDataMap = {
    "M": {alpha: "Male", kanji: "男"},
    "F": {alpha: "Female", kanji: "女"}
};

export function sexAsKanji(sex){
    let data = sexDataMap[sex];
    return data ? data.kanji : data;
}
