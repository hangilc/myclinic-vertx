export const ConductKindHikaChuusha = 0;
export const ConductKindJoumyakuChuusha = 1;
export const ConductKindOtherChuusha = 2;
export const ConductKindGazou = 3;

export function conductKindToKanji(kindCode){
    switch(kindCode){
        case ConductKindHikaChuusha: return "皮下・筋肉注射";
        case ConductKindJoumyakuChuusha: return "静脈注射";
        case ConductKindOtherChuusha: return "その他の注射";
        case ConductKindGazou: return "画像";
        default: return "" + kindCode;
    }
}

export const DrugCategoryNaifuku = 0;
export const DrugCategoryTonpuku = 1;
export const DrugCategoryGaiyou  = 2;
export const DrugCategoryInjection  = 3;


export const DiseaseEndReasonNotEnded = 'N';
export const DiseaseEndReasonStopped = 'S';
export const DiseaseEndReasonCured = 'C';
export const DiseaseEndReasonDead = 'D';

export function diseaseEndReasonToKanji(code){
    switch(code){
        case DiseaseEndReasonNotEnded: return "継続";
        case DiseaseEndReasonStopped: return "中止";
        case DiseaseEndReasonCured: return "治癒";
        case DiseaseEndReasonDead: return "死亡";
        default: return "" + code;
    }
}

export const suspMaster = {
    shuushokugocode: 8002,
    name: "の疑い"
}


