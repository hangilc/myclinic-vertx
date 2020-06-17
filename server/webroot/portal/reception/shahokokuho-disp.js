export class ShahokokuhoDisp {
    constructor(map) {
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaKigouElement = map.hihokenshaKigou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.honninElement = map.honnin;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.koureiElement = map.kourei;
    }

    init(){

    }

    set(shahokokuho){
        if( shahokokuho ){
            this.hokenshaBangouElement.text(shahokokuho.hokenshaBangou);
            this.hihokenshaKigouElement.text(shahokokuho.hihokenshaKigou);
            this.hihokenshaBangouElement.text(shahokokuho.hihokenshaBangou);
            this.honninElement.text(shahokokuho.honnin);
            this.validFromElement.text(shahokokuho.validFrom);
            this.validUptoElement.text(shahokokuho.validUpto);
            this.koureiElement.text(shahokokuho.kourei);
        } else {
            this.hokenshaBangouElement.text("");
            this.hihokenshaKigouElement.text("");
            this.hihokenshaBangouElement.text("");
            this.honninElement.text("");
            this.validFromElement.text("");
            this.validUptoElement.text("");
            this.koureiElement.text("");
        }
    }
}