export class ShahokokuhoForm {
    constructor(map){
        this.error = null;
        this.hokenshabangouElement = map.hokenshabangou;
        this.hihokenshaKigouElement = map.hihokenshaKigou;
        this.hihokenshaBangouElement = map.hihokenshaBangou;
        this.honninElement = map.honnin;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.koureiElement = map.kourei;
    }

    init(patientId){
        this.patientId = patientId;
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

    get(){
        let hokenshabangouInput = this.hokenshabangouElement.val();
        if( hokenshabangouInput === "" ){
            this.error = "保険者番号が入力されていません。";
            return undefined;
        }
        let hokenshabangou = parseInt(hokenshabangouInput);
        if( isNaN(hokenshabangou) ){
            this.error = "保険者番号の入力が不適切です。";
            return undefined;
        }
        let hihokenshaKigou = this.hihokenshaKigouElement.val();
        let hihokenshaBangou = this.hihokenshaBangouElement.val();
        if( hihokenshaKigou === "" && hihokenshaBangou === "" ){
            this.error = "被保険者情報が入力されていません。";
            return undefined;
        }
        let honninInput = this.honninElement.val();
        let honnin = parseInt(honninInput);
        if( !(honnin === 1 || honnin == 0) ){
            this.error = "本人・家族の入力が不適切です。";
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
        let koureiInput = this.koureiElement.val();
        let kourei = parseInt(koureiInput);
        if( !(kourei >= 0 && kourei <= 3) ){
            this.error = "高齢の入力が不適切です。";
            return undefined;
        }
        return {
            patientId: this.patientId,
            hokenshabangou,
            hihokenshaKigou,
            hihokenshaBangou,
            honnin,
            validFrom,
            validUpto,
            kourei
        };
    }
}