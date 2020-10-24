export class KouhiForm {
    constructor(map) {
        this.error = null;
        this.futanshaElement = map.futansha;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
    }

    init(){
        return this;
    }

    set(kouhi){
        if( kouhi ){
            this.futanshaElement.val(kouhi.futansha);
            this.jukyuushaElement.val(kouhi.jukyuusha);
            this.validFromElement.val(kouhi.validFrom);
            this.validUptoElement.val(kouhi.validUpto);
        } else {
            this.futanshaElement.val(null);
            this.jukyuushaElement.val(null);
            this.validFromElement.val(null);
            this.validUptoElement.val(null);
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

    get(kouhiId, patientId){
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
            console.log(this.validFromElement.getError());
            this.error = "開始日の入力が不適切です。";
            return undefined;
        }
        let validUpto = this.validUptoElement.val();
        if( !validUpto ){
            this.error = "終了日の入力が不適切です。";
            return undefined;
        }
        return {
            kouhiId,
            patientId,
            futansha,
            jukyuusha,
            validFrom,
            validUpto
        };
    }
}
