import {DispTable} from "./disp-table.js";
import {validFromRep, validUptoRep} from "./form-util.js";

export class ShahokokuhoDisp extends DispTable {
    constructor(shahokokuho){
        super();
        this.add("保険者番号", shahokokuho.hokenshaBangou);
        this.add("被保険者", `${shahokokuho.hihokenshaKigou} - ${shahokokuho.hihokenshaBangou}`);
        this.add("本人・家族", honninRep(shahokokuho.honnin));
        this.add("開始日", validFromRep(shahokokuho.validFrom));
        this.add("終了日", validUptoRep(shahokokuho.validUpto));
        this.add("高齢", koureiRep(shahokokuho.kourei));
        this.set(shahokokuho);
    }
}

function honninRep(honnin){
    if( honnin === 0 ){
        return "家族";
    } else {
        return "本人";
    }
}

function koureiRep(kourei){
    if( kourei >= 1 ){
        return `${kourei}割`;
    } else {
        return "";
    }
}

