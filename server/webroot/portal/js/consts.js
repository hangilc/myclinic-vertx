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
