export class CurrentVisitManager {
    constructor(){
        this.currentVisitId = 0;
        this.tempVisitId = 0;
    }

    getCurrentVisitId(){
        return this.currentVisitId;
    }

    getTempVisitId(){
        return this.tempVisitId;
    }

    setCurrentVisitId(visitId){
        this.tempVisitId = 0;
        this.currentVisitId = visitId;
    }

    setTempVisitId(visitId){
        if( this.currentVisitId === 0 ){
            this.tempVisitId = visitId;
        }
    }

    resolveCopyTarget(){
        if( this.currentVisitId > 0 ){
            return this.currentVisitId;
        } else {
            return this.tempVisitId;
        }
    }

}
