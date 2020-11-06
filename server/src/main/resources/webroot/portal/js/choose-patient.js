import * as kanjidate from "./kanjidate.js";

let html = `
  <div class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">患者選択</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body choose-patient-form">
        <form class="form-inline">
            <input class="form-control choose-patient-input"/> 
            <button type="submit" 
                class="form-control ml-2 choose-patient-search-button">検索</button>
        </form>
        <select class="form-control mt-2 form-control choose-patient-select" size="10">
        </select>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary choose-patient-enter">入力</button>
        <button type="button" class="btn btn-secondary choose-patient-cancel">キャンセル</button>
      </div>
    </div>
  </div>
</div>      
`;

function cmp(...props){
    return (a, b) => {
        for(let p of props){
            let pa = a[p];
            let pb = b[p];
            if( pa < pb ){
                return -1;
            } else if( pa > pb ){
                return 1;
            }
        }
        return 0;
    };
}

let cmpPatient = cmp("lastNameYomi", "firstNameYomi", "patientId");


function makePatientLabel(patient){
    let patientIdRep = ("" + patient.patientId).padStart(4, "0");
    let birthday = kanjidate.sqldateToKanji(patient.birthday);
    return `(${patientIdRep}) ${patient.lastName}${patient.firstName} (${birthday}生)`;

}

export async function choosePatient() {
    return new Promise((resolve, fail) => {
        let patient = null;
        let modal = $(html);
        modal.on("hide.bs.modal", event => {
            modal.remove();
            resolve(patient);
        });
        modal.on("shown.bs.modal", event => { modal.find(".choose-patient-input").focus(); });
        modal.find(".choose-patient-enter").on("click", event => {
            let data = modal.find(".choose-patient-select option:selected").data("patient");
            patient = data;
            modal.modal('hide');
        });
        modal.find(".choose-patient-cancel").on("click", event => {
            modal.modal('hide');
        });
        modal.find(".choose-patient-form").on("submit", async event => {
            event.preventDefault();
            let text = modal.find(".choose-patient-input").val();
            let result = await rest.searchPatient(text);
            result.sort(cmpPatient);
            let select = modal.find(".choose-patient-select").html("");
            for(let p of result){
                let opt = $("<option>").text(makePatientLabel(p)).data("patient", p);
                select.append(opt);
            }
        });
        $("body").append(modal);
        modal.modal('show');
    });
}