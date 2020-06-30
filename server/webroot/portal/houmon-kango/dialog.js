export class Dialog {

    constructor(ele){
        this.ele = ele;
        this.dialogResult = undefined;
        this.ele.on("hidden.bs.modal", event => {
            if( this.resolve ){
                this.resolve(this.dialogResult);
            }
        });
    }

    close(result){
        this.dialogResult = result;
        this.ele.modal("hide");
    }

    open(){
        return new Promise(resolve => {
            this.resolve = resolve;
            this.ele.modal("show");
        });
    }
}