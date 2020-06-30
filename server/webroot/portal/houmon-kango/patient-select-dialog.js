import {Dialog} from "./dialog.js";
import {PatientSearch} from "./patient-search.js";
import {parseElement} from "./component.js";

let template = `
<div class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">患者選択</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <form class="form-inline x-form">
            <input class="form-control x-input"/> 
            <button type="submit" 
                class="form-control ml-2 x-search-button">検索</button>
        </form>
        <select class="form-control mt-2 form-control x-select" size="10">
        </select>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary x-enter">入力</button>
        <button type="button" class="btn btn-secondary x-cancel">キャンセル</button>
      </div>
    </div>
  </div>
</div>      
`;

export class PatientSelectDialog extends Dialog {

    constructor(rest){
        super($(template));
        let map = parseElement(this.ele);
        this.patientSearch = new PatientSearch(rest, map.form, map.input, map.select);
        this.ele.on("shown.bs.modal", event => this.patientSearch.focus());
        map.enter.on("click", event => this.doEnter());
        map.cancel.on("click", event => this.close());
    }

    doEnter(){
        let patient = this.patientSearch.getSelectedData();
        if( patient ){
            this.close(patient);
        }
    }

}