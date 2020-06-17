export class KoukikoureiDisp {
    constructor(map) {
        this.hokenshaBangouElement = map.hokenshaBangou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.futanWariElement = map.futanWari;
    }

    init(){
        return this;
    }

    set(koukikourei){
        if( koukikourei ){
            this.hokenshaBangouElement.text(koukikourei.hokenshaBangou);
            this.hihokenshaBangouElement.text(koukikourei.hihokenshaBangou);
            this.validFromElement.text(koukikourei.validFrom);
            this.validUptoElement.text(koukikourei.validUpto);
            this.futanWariElement.text(koukikourei.futanWari);
        } else {
            this.hokenshaBangouElement.text("");
            this.hihokenshaBangouElement.text("");
            this.validFromElement.text("");
            this.validUptoElement.text("");
            this.futanWariElement.text("");
        }
        return this;
    }
}