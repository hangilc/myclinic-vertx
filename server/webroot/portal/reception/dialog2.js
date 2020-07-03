
let template = `
    <div class="modal" tabindex="-1" role="dialog" data-backdrop="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"></h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body"> </div>
                <div class="modal-footer"> </div>
            </div>
        </div>
    </div>
`;

export class Dialog {

    constructor(ele){
        if( !ele ){
            ele = $(template);
        }
        this.ele = ele;
        this.dialogResult = undefined;
        ele.on("hide.bs.modal", event => {
            if( this.resolve ){
                this.resolve(this.dialogResult);
            }
        });
        ele.on("shown.bs.modal", event => {
            if( this.focus ){
                this.focus();
            }
        });
    }

    setDialogTitle(title){
        this.ele.find(".modal-title").text(title);
    }

    appendToBody(element){
        this.ele.find(".modal-body").append(element);
    }

    appendToFooter(element){
        this.ele.find(".modal-footer").append(element);
    }

    close(result){
        this.dialogResult = result;
        this.ele.modal("hide");
    }

    async open(){
        return new Promise(resolve => {
            this.resolve = resolve;
            this.ele.modal("show");
        });
    }

}