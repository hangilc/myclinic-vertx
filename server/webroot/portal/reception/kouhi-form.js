export class KouhiForm {
    constructor(map) {
        this.error = null;
        this.futanshaElement = map.futansha;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
    }

    init(patientId){
        this.patientId = patientId;
    }

    set(){

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
        let patientId = this.patientId;
        if( !(patientId > 0) ){
            this.error = "患者が設定されていません。";
            return undefined;
        }
        let futanshaInput = this.futanshaElement.val();
        if( futanshaInput === "" ){
            this.error = "負担者番号が入力されていません。";
            return undefined;
        }
        let futansha = parseInt(futanshaInput);
        if( isNaN(futansha) ){
            this.error = "負担者番号の入力が不適切です。";
            return undefined;
        }
        let jukyuushaInput = this.jukyuushaElement.val();
        if( jukyuushaInput === "" ){
            this.error = "受給者番号が入力されていません。";
            return undefined;
        }
        let jukyuusha = parseInt(jukyuushaInput);
        if( isNaN(jukyuusha) ){
            this.error = "受給者番号の入力が不適切です。";
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
        return {
            patientId,
            futansha,
            jukyuusha,
            validFrom,
            validUpto
        };
    }
}
