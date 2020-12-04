import {createElementFrom} from "./create-element-from.js";

let tmpl = `
    <div class="modal" tabindex="-1" data-backdrop="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"></h5>
                    <button type="button" class="close">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body"> </div>
                <div class="modal-footer"> </div>
            </div>
        </div>
    </div>
`;

export class Dialog {
    constructor(){
        this.ele = createElementFrom(tmpl);
        this.ele.querySelector(".modal-header button.close")
            .addEventListener("click", event => this.close(undefined));
    }

    setTitle(title){
        this.ele.querySelector(".modal-title").innerText = title;
    }

    setLarge(){
        this.ele.querySelector(".modal-dialog").classList.add("modal-lg");
    }

    setSmall(){
        this.ele.querySelector(".modal-dialog").classList.add("modal-sm");
    }

    getBody(){
        return this.ele.querySelector(".modal-body");
    }

    getFooter(){
        return this.ele.querySelector(".modal-footer");
    }

    close(retVal){
        this.resolve(retVal);
        $(this.ele).modal("hide");
        $(this.ele).modal("dispose");
        $(this.ele).remove();
    }

    async open(){
        return new Promise(resolve => {
            this.resolve = resolve;
            $(this.ele).modal("show");
            this.ele.dispatchEvent(new Event("opened"));
        });
    }
}