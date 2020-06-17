export class RoujinDisp {
    constructor(map) {
        this.shichousonElement = map.shichouson;
        this.jukyuushaElement = map.jukyuusha;
        this.validFromElement = map.validFrom;
        this.validUptoElement = map.validUpto;
        this.futanWariElement = map.futanWari;
    }

    init(){
        return this;
    }

    set(roujin){
        if( roujin ){
            this.shichousonElement.text(roujin.shichouson);
            this.jukyuushaElement.text(roujin.jukyuusha);
            this.validFromElement.text(roujin.validFrom);
            this.validUptoElement.text(roujin.validUpto);
            this.futanWariElement.text(roujin.futanWari);
        } else {
            this.shichousonElement.text("");
            this.jukyuushaElement.text("");
            this.validFromElement.text("");
            this.validUptoElement.text("");
            this.futanWariElement.text("");
        }
        return this;
    }
}