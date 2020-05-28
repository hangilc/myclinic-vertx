import * as kanjidate from "../js/kanjidate.js";

export class Data {
    constructor(data) {
        this.data = data;
    }

    getSubtitle1FromDate(){
        let m = this.gengouDate("令和", this.data["subtitle1.from.nen"],
            this.data["subtitle1.from.month"], this.data["subtitle1.from.day"]);
        return m ? m.format("YYYY-MM-DD") : null;
    }

    getSubtitle1UptoDate(){
        let m = this.gengouDate("令和", this.data["subtitle1.upto.nen"],
            this.data["subtitle1.upto.month"], this.data["subtitle1.upto.day"]);
        return m ? m.format("YYYY-MM-DD") : null;
    }

    getRecipient(){
        return this.data.recipient;
    }

    gengouDate(gengou, nen, month, day){
        nen = parseInt(nen);
        month = parseInt(month);
        day = parseInt(day);
        if (!isNaN(nen) && !isNaN(month) && !isNaN(day)) {
            let year = kanjidate.gengouToSeireki(gengou, nen);
            if (year != null) {
                return moment({year, month: month - 1, date: day});
            }
        }
        return null;
    }
}