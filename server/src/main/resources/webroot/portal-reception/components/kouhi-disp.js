import {DispTable} from "./disp-table.js";
import {validFromRep, validUptoRep} from "./form-util.js";

export class KouhiDisp extends DispTable {
    constructor(kouhi){
        super();
        this.add("負担者番号", kouhi.futansha);
        this.add("受給者番号", kouhi.jukyuusha);
        this.add("開始日", validFromRep(kouhi.validFrom));
        this.add("終了日", validUptoRep(kouhi.validUpto));
    }
}

