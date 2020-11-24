import {DispTable} from "./disp-table.js";
import {validFromRep, validUptoRep} from "./form-util.js";

export class KoukikoureiDisp extends DispTable {
    constructor(koukikourei){
        super();
        this.add("保険者番号", koukikourei.hokenshaBangou);
        this.add("被保険者番号", koukikourei.hihokenshaBangou);
        this.add("開始日", validFromRep(koukikourei.validFrom));
        this.add("終了日", validUptoRep(koukikourei.validUpto));
        this.add("負担割", `${koukikourei.futanWari}割`);
        this.set(koukikourei);
    }
}

