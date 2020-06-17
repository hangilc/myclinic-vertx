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

    set(){
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
        let hokenshaBangou = parseInt(hokenshaBangouInput);
        if( isNaN(hokenshaBangou) ){
            this.error = "保険者番号の入力が不適切です。";
            return undefined;
        }
        let hihokenshaBangouInput = this.hihokenshaBangouElement.val();
        if( hihokenshaBangouInput === "" ){
            this.error = "被保険者番号が入力されていません。";
            return undefined;
        }
        let hihokenshaBangou = parseInt(hihokenshaBangouInput);
        if( isNaN(hihokenshaBangou) ){
            this.error = "被保険者番号の入力が不適切です。";
            return undefined;
        }
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