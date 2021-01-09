export const ConductKindHikaChuusha = 0;
export const ConductKindJoumyakuChuusha = 1;
export const ConductKindOtherChuusha = 2;
export const ConductKindGazou = 3;

export function conductKindToKanji(kindCode) {
    switch (kindCode) {
        case ConductKindHikaChuusha:
            return "皮下・筋肉注射";
        case ConductKindJoumyakuChuusha:
            return "静脈注射";
        case ConductKindOtherChuusha:
            return "その他の注射";
        case ConductKindGazou:
            return "画像";
        default:
            return "" + kindCode;
    }
}

export const DrugCategoryNaifuku = 0;
export const DrugCategoryTonpuku = 1;
export const DrugCategoryGaiyou = 2;
export const DrugCategoryInjection = 3;


export const DiseaseEndReasonNotEnded = 'N';
export const DiseaseEndReasonStopped = 'S';
export const DiseaseEndReasonCured = 'C';
export const DiseaseEndReasonDead = 'D';

export function diseaseEndReasonToKanji(code) {
    switch (code) {
        case DiseaseEndReasonNotEnded:
            return "継続";
        case DiseaseEndReasonStopped:
            return "中止";
        case DiseaseEndReasonCured:
            return "治癒";
        case DiseaseEndReasonDead:
            return "死亡";
        default:
            return "" + code;
    }
}

export const suspMaster = {
    shuushokugocode: 8002,
    name: "の疑い"
}

export let MeisaiSections = [
    {name: "ShoshinSaisin", label: "初・再診料"},
    {name: "IgakuKanri", label: "医学管理等"},
    {name: "Zaitaku", label: "在宅医療"},
    {name: "Kensa", label: "検査"},
    {name: "Gazou", label: "画像診断"},
    {name: "Touyaku", label: "投薬"},
    {name: "Chuusha", label: "注射"},
    {name: "Shochi", label: "処置"},
    {name: "Sonota", label: "その他"}
];

export const WqueueStateWaitExam = 0;
export const WqueueStateInExam = 1;
export const WqueueStateWaitCashier = 2;
export const WqueueStateWaitDrug = 3;
export const WqueueStateWaitReExam = 4;
export const WqueueStateWaitAppoint = 5;

const WqueueStateRep = {
    [WqueueStateWaitExam]: "診待",
    [WqueueStateInExam]: "診中",
    [WqueueStateWaitCashier]: "会待",
    [WqueueStateWaitDrug]: "薬待",
    [WqueueStateWaitReExam]: "再待"
};

export function wqueueStateCodeToRep(code) {
    return WqueueStateRep[code];
}

export function sexToRep(sex, suffix="") {
    switch (sex) {
        case "M":
            return "男" + suffix;
        case "F":
            return "女" + suffix;
        default:
            return sex;
    }
}




