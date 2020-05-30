export class Dialog {
    constructor(props){
        this.dialog = props["dialog"];
        this.title = props["title"];
        this.body = props["body"];
        this.footer = props["footer"];
    }

    onClose(){
        return null;
    }

    async open (){
        return new Promise((resolve) => {
            let fn = (event) => resolve(this.onClose());
            this.dialog.on("hidden.bs.modal", event => {
                resolve(this.onClose());
            });
            this.dialog.modal("show");
        });
    }
}