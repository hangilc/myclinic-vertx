export class KoukikoureiForm {
    constructor(map) {
        this.error = null;
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
            this.hokenshaBangouElement.val(koukikourei.hokenshaBangou);
            this.hihokenshaBangouElement.val(koukikourei.hihokenshaBangou);
            this.validFromElement.val(koukikourei.validFrom);
            this.validUptoElement.val(koukikourei.validUpto);
            this.futanWariElement.val(koukikourei.futanWari);
        } else {
            this.hokenshaBangouElement.val(null);
            this.hihokenshaBangouElement.val(null);
            this.validFromElement.val(null);
            this.validUptoElement.val(null);
            this.futanWariElement.val(null);
        }
        return this;
    }

    getError(){
        let err = this.error;
        this.error = null;
        return err;
    }

    clearValidUpto(){
        this.validUptoElement.val(null);
    }

    get(koukikoureiId, patientId){
        let hokenshaBangouInput = this.hokenshaBangouElement.val();
        if( hokenshaBangouInput === "" ){
            this.error = "保険者番号が入力されていません。";
            return undefined;
        }
        if( hokenshaBangouInput.length !== 8 ){
            if( !confirm("保険者番号（後期高齢保険）が８桁でありませんが、そのまま入力しますか？") ){
                return undefined;
            }
        }
        let hokenshaBangou = hokenshaBangouInput;
        let hihokenshaBangouInput = this.hihokenshaBangouElement.val();
        if( hihokenshaBangouInput === "" ){
            this.error = "被保険者番号が入力されていません。";
            return undefined;
        }
        if( hihokenshaBangouInput.length !== 8 ){
            if( !confirm("被保険者番号（後期高齢保険）が８桁でありませんが、そのまま入力しますか？") ){
                return undefined;
            }
        }
        let hihokenshaBangou = hihokenshaBangouInput;
        let validFrom = this.validFromElement.val();
        if( !validFrom ){
            this.error = "開始日の入力が不適切です。";
            return undefined;
        }
        let validUpto = this.validUptoElement.val();
        if( !validUpto ){
            this.error = "終了日の入力が不適切です。";
            return undefined;
        }
        let futanWariInput = this.futanWariElement.val();
        let futanWari = parseInt(futanWariInput);
        if( !(futanWari >= 1 && futanWari <= 3) ){
            this.error = "負担割の入力が不適切です。";
            return undefined;
        }
        return {
            koukikoureiId,
            patientId,
            hokenshaBangou,
            hihokenshaBangou,
            validFrom,
            validUpto,
            futanWari
        };
    }
}