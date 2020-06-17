export class KouhiDisp {
    constructor(map) {
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
            this.futanshaElement.text(kouhi.futansha);
            this.jukyuushaElement.text(kouhi.jukyuusha);
            this.validFromElement.text(kouhi.validFrom);
            this.validUptoElement.text(kouhi.validUpto);
        } else {
            this.futanshaElement.text("");
            this.jukyuushaElement.text("");
            this.validFromElement.text("");
            this.validUptoElement.text("");
        }
        return this;
    }
}