export class RecordManager {
    constructor(){
        this.records = [];
    }

    add(record){
        this.records.push(record);
    }

    clear(){
        this.records = [];
    }

    forEachRecord(cb){
        this.records.forEach(record => cb(record));
    }

}