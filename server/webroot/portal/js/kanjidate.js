const gengouList = [
    {
        name: "令和",
        start: "2019-05-01",
        year: 2019,
        alpha: "Reiwa"
    },
    {
        name: "平成",
        start: "1989-01-08",
        year: 1989,
        alpha: "Heisei"
    },
    {
        name: "昭和",
        start: "1926-12-25",
        year: 1926,
        alpha: "Shouwa"
    },
    {
        name: "大正",
        start: "1912-07-30",
        year: 1912,
        alpha: "Taishou"
    },
    {
        name: "明治",
        start: "1873-01-01",
        year: 1873,
        alpha: "Meiji"
    }
];

function toSqldate(year, month, day){
    let y = ("" + year).padStart(4, "0");
    let m = ("" + month).padStart(2, "0");
    let d = ("" + day).padStart(4, "0");
    return y + m + d;
}

export function parseSqldate(sqldate){
    let year = +sqldate.slice(0, 4);
    let month = +sqldate.slice(5, 7);
    let day = +sqldate.slice(8, 10);
    return {year, month, day};
}

export function seirekiToGengou(year, month, day){
    let sqldate = toSqldate(year, month, day);
    for(let g of gengouList){
        if( sqldate >= g.start ){
            return [g.name, year - g.year + 1];
        }
    }
}

export function gengouToSeireki(gengou, nen){
    for(let g of gengouList){
        if( g.name === gengou ){
            return g.year + nen - 1;
        }
    }
    return null;
}

export function sqldateToKanji(sqldate, opts){
    if( !opts ){
        opts = {};
    }
    let d = parseSqldate(sqldate);
    let [g, n] = seirekiToGengou(d.year, d.month, d.day);
    return `${g}${n}年${d.month}月${d.day}日`;
}

export function sqldateToKanjiData(sqldate){
    let d = parseSqldate(sqldate);
    let [g, n] = seirekiToGengou(d.year, d.month, d.day);
    return {
        gengou: g,
        nen: n,
        year: d.year,
        month: d.month,
        day: d.day
    };
}