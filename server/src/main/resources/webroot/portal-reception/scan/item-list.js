import * as paperscan from "../../js/paper-scan.js";

export class ItemList {
    constructor(rest, printAPI) {
        this.rest = rest;
        this.printAPI = printAPI;
        this.items = [];
        this.state = "before-upload";
    }

    addScan(scannedFile){

    }

    remove(item){
        this.items = this.items.filter(i => i !== item);
    }

    setScannersInUse(scannersInUse){
        this.items.forEach(item => item.setScannersInUse(scannersInUse));
    }

    setIsListUploading(value){
        this.items.forEach(item => item.setIsListUploading(value));
    }

    updateDiabled(){
        this.items.forEach(item => item.updateDisabled());
    }

    setStateBeforeUpload(){
        this.state = "before-upload";
    }

    isStateBeforeUpload(){
        return this.state === "before-upload";
    }

    setStateUploading(){
        this.state = "uploading";
    }

    isStateUploading(){
        return this.state === "uploading";
    }

    setStateUploadFail(){
        this.state = "upload-fail";
    }

    isStateUploadFail(){
        return this.state === "upload-fail";
    }

    setStateUploaded(){
        this.state = "uploaded";
    }

    isStateUploaded(){
        return this.state === "uploaded";
    }

    renameUploadNames(patientId, tag){
        let patientIdTag = patientId ? ("" + patientId) : "????";
        let timestamp = paperscan.getTimestamp();
        let items = this.items;
        if (items.length === 1) {
            let item = items[0];
            setUploadName(item, patientIdTag, tag, timestamp);
            let ext = paperscan.getFileExtension(item.getScannedFile());
            item.setUploadName(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "", ext));
            item.updateUploadNameUI();
        } else {
            let ser = 1;
            for (let item of items) {
                setUploadName(item, patientIdTag, tag, timestamp);
                let ext = paperscan.getFileExtension(item.getScannedFile());
                item.setUploadName(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, "" + ser, ext));
                item.updateUploadNameUI();
                ser += 1;
            }
        }

    }

    updateDisabled(isScanning){
        this.items.forEach(item => {
            item.updateDisabled(isScanning, this);
        });
    }

}

function setUploadName(item, patientIdTag, tag, timestamp, ser=""){
    let ext = paperscan.getFileExtension(item.getScannedFile());
    item.setUploadName(paperscan.createPaperScanFileName(patientIdTag, tag, timestamp, ser, ext));
    item.updateUploadNameUI();
}