export class CurrentPatientManager {
    constructor() {
        this.currentPatientId = 0;
        this.eventElement = $("<div>");
    }

    onChanged(cb){
        this.eventElement.on("change", cb);
    }

    triggerChanged(){
        this.eventElement.trigger("change", this.currentPatientId);
    }

    setCurrentPatientId(patientId){
        this.currentPatientId = patientId;
        this.triggerChanged();
    }

    getCurrentPatientId(){
        return this.currentPatientId;
    }

}