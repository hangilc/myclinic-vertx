import * as HokenUtil from "../js/hoken-util.js";

export class HokenHelper {
    constructor(rest) {
        this.rest = rest;
    }

    async fetchAvailableHoken(patientId, date){
        let hoken = await this.rest.listAvailableHoken(patientId, date);
        let hokenList = HokenUtil.hokenToHokenList(hoken);
        await this.extendHokenList(hokenList);
        return hokenList;
    }

    async fetchAllHoken(patientId){
        let hokenList = await this.rest.listAllHoken(patientId);
        await this.extendHokenList(hokenList);
        return hokenList;
    }

    async extendHokenList(hokenList){
        console.log("hokenList", hokenList);
        for(let shahokokuho of hokenList.shahokokuhoList){
            shahokokuho.rep = await this.rest.shahokokuhoRep(shahokokuho);
        }
        for(let koukikourei of hokenList.koukikoureiList){
            koukikourei.rep = await this.rest.koukikoureiRep(koukikourei);
        }
        for(let roujin of hokenList.roujinList){
            roujin.rep = await this.rest.roujinRep(roujin);
        }
        for(let kouhi of hokenList.kouhiList){
            kouhi.rep = await this.rest.kouhiRep(kouhi);
        }
    }
}