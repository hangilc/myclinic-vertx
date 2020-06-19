export class PracticeState {
    constructor(rest) {
        this.rest = rest;
        this.patientId = 0;
        this.visitId = 0;
        this.tempVisitId = 0;
        this.patientIdChangedCallbacks = [];
        this.visitIdChangedCallbacks = [];
        this.tempVisitIdChangedCallbacks = [];
    }

    getPatientId() {
        return this.patientId;
    }

    getVisitId() {
        return this.visitId;
    }

    getTempVisitId() {
        return this.tempVisitId;
    }

    onPatientIdChanged(cb) {
        this.patientIdChangedCallbacks.push(cb);
    }

    onVisitIdChanged(cb){
        this.visitIdChangedCallbacks.push(cb);
    }

    onTempVisitIdChanged(cb){
        this.tempVisitIdChangedCallbacks.push(cb);
    }

    startPatient(patientId) {
        if( this.patientId === 0 && this.visitId === 0 ){
            this.patientId = patientId;
            this.tempVisitId = 0;
            this.patientIdChangedCallbacks.forEach(cb => cb(patientId));
        } else {
            let msg = "Cannot start patient. " +
                `patientId(${this.patientId}), visitId(${this.visitId}), tempVisitId(${this.tempVisitId})`;
            throw new Error(msg);
        }
    }

    endPatient(){
        if( this.patientId > 0 && this.visitId === 0 ){
            this.patientId = 0;
            this.tempVisitId = 0;
            this.patientIdChangedCallbacks.forEach(cb => cb(this.patientId));
            this.tempVisitIdChangedCallbacks.forEach(cb => cb(this.tempVisitId));
        } else {
            let msg = "Cannot end patient. " +
                `patientId(${this.patientId}), visitId(${this.visitId}), tempVisitId(${this.tempVisitId})`;
            throw new Error(msg);
        }
    }

    async startExam(patientId, visitId){
        if( this.patientId === 0 && this.visitId === 0 && this.tempVisitId === 0 ){
            await this.rest.startExam(visitId);
            this.patientId = patientId;
            this.visitId = visitId;
            this.tempVisitId = 0;
            this.patientIdChangedCallbacks.forEach(cb => cb(this.patientId));
            this.visitIdChangedCallbacks.forEach(cb => cb(this.visitId));
            this.tempVisitIdChangedCallbacks.forEach(cb => cb(this.tempVisitId));
        } else {
            let msg = "Cannot start exam. " +
                `patientId(${this.patientId}), visitId(${this.visitId}), tempVisitId(${this.tempVisitId})`;
            throw new Error(msg);
        }
    }

    async suspendExam(visitId){
        if( this.visitId > 0 && this.visitId === visitId ){
            await this.rest.suspendExam(this.visitId);
            this.patientId = 0;
            this.visitId = 0;
            this.tempVisitId = 0;
            this.patientIdChangedCallbacks.forEach(cb => cb(this.patientId));
            this.visitIdChangedCallbacks.forEach(cb => cb(this.visitId));
            this.tempVisitIdChangedCallbacks.forEach(cb => cb(this.tempVisitId));
        } else {
            let msg = "Cannot suspend exam. " +
                `patientId(${this.patientId}), visitId(${this.visitId}), tempVisitId(${this.tempVisitId})`;
            throw new Error(msg);
        }
    }

    async endExam(visitId, charge){
        if( this.patientId > 0 && this.visitId > 0 && this.visitId === visitId ){
            await this.rest.endExam(this.visitId, charge);
            this.patientId = 0;
            this.visitId = 0;
            this.tempVisitId = 0;
            this.patientIdChangedCallbacks.forEach(cb => cb(this.patientId));
            this.visitIdChangedCallbacks.forEach(cb => cb(this.visitId));
            this.tempVisitIdChangedCallbacks.forEach(cb => cb(this.tempVisitId));
        } else {
            let msg = "Cannot end exam. " +
                `patientId(${this.patientId}), visitId(${this.visitId}), tempVisitId(${this.tempVisitId})`;
            throw new Error(msg);
        }
    }

    async deleteVisit(visitId){
        if( visitId > 0 ){
            await this.rest.deleteVisit(visitId);
            if( visitId === this.visitId ){
                this.visitId = 0;
                this.visitIdChangedCallbacks.forEach(cb => cb(this.visitId));
            } else if( visitId === this.tempVisitId ){
                this.tempVisitId = 0;
                this.tempVisitIdChangedCallbacks.forEach(cb => cb(this.tempVisitId));
            }
        } else {
            throw new Error("Cannot delete visit: visitId is zero.");
        }
    }

    setTempVisitId(tempVisitId){
        if( this.visitId !== 0 ){
            throw new Error("現在診察中なので暫定診察を設定できません。");
        }
        if( tempVisitId !== this.tempVisitId ){
            this.tempVisitId = tempVisitId;
            this.tempVisitIdChangedCallbacks.forEach(cb => cb(tempVisitId));
        }
    }

}