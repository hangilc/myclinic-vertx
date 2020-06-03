export class CurrentPatientManager {
    constructor() {
        this.currentPatientId = 0;
        this.eventElement = $("<div>");
    }

    onChanged(cb){
        this.eventElement.on("change", cb);
    }

    triggerChange(){
        this.eventElement.trigger("change", {
            currentPatientId: this.currentPatientId
        });
    }

    setCurrentPatientId(patientId){
        this.currentPatientId = patientId;
    }

    getCurrentPatientId(){
        return this.currentPatientId;
    }

}