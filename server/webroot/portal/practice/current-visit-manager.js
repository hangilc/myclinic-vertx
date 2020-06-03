export class CurrentVisitManager {
    constructor(){
        this.eventElement = $("<div>");
        this.currentVisitId = 0;
        this.tempVisitId = 0;
    }

    getCurrentVisitId(){
        return this.currentVisitId;
    }

    getTempVisitId(){
        return this.tempVisitId;
    }

    onChanged(cb){
        this.eventElement.on("change", cb);
    }

    triggerChange(){
        this.eventElement.trigger("change", {
            currentVisitId: this.currentVisitId,
            tempVisitId: this.tempVisitId
        });
    }

    setCurrentVisitId(visitId){
        this.tempVisitId = 0;
        this.currentVisitId = visitId;
        this.triggerChange();
        return { success: true };
    }

    setTempVisitId(visitId){
        if( this.currentVisitId === 0 ){
            this.tempVisitId = visitId;
            this.triggerChange();
            return { success: true };
        } else {
            return { success: false, message: "現在診察中のため、暫定診察を設定できません。" };
        }
    }

}
