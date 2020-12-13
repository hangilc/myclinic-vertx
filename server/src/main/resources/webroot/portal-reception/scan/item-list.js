import * as paperscan from "../../js/paper-scan.js";
import {extendProp} from "../../js/extend-prop.js";
import {ScannedItem} from "./scanned-item.js";
import {createElementFrom} from "../../js/create-element-from.js";

let tmpl = `
<div></div>
`;

export class ItemList {
    constructor(prop) {
        this.prop = extendProp(prop, {});
        this.items = [];
        this.state = "before-upload";
        this.ele = createElementFrom(tmpl);
    }

    addScan(scannedFile, uploadName){
        let item = new ScannedItem(this.prop, scannedFile, "");
        this.items.push(item);
        this.ele.append(item.ele);
        this.renameUploadNames();
    }

    async deleteScannedFiles(){
        for(let item of this.items){
            await item.deleteScannedFile();
        }
    }

    renameUploadNames(){
        let patientIdTag = this.makePatientIdTag();
        let tag = this.prop.getTag();
        let timestamp = paperscan.getTimestamp();
        let items = this.items;
        if (items.length === 1) {
            let item = items[0];
            let ext = paperscan.getFileExtension(item.getScannedFile());
            let uploadName = paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "", ext);
            item.setUploadFile(uploadName);
        } else {
            let ser = 1;
            for (let item of items) {
                let ext = paperscan.getFileExtension(item.getScannedFile());
                let uploadName = paperscan.createPaperScanFileName(patientIdTag, tag, timestamp,
                    "" + ser, ext);
                item.setUploadFile(uploadName);
                ser += 1;
            }
        }
    }

    isEmpty(){
        return this.items.length === 0;
    }

    makePatientIdTag(){
        let patient = this.prop.patient;
        if( patient ){
            return "" + patient.patientId;
        } else {
            return "????";
        }
    }

    async upload(){
        for(let item of this.items){
            if( !item.isUploaded() ){
                let ok = await item.upload();
                if( !ok ){
                    return false;
                }
            }
        }
        return true;
    }

    updateDisabled(){

    }

}

