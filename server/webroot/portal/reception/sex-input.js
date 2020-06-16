export class SexInput {
    constructor(form, name) {
        this.form = form;
        this.name = name;
    }

    val(value){
        if( arguments.length >= 1 ){
            let sel = `input[type=radio][name=${this.name}][value=${value}]`;
            this.form.find(sel).prop("checked", true);
        } else {
            let sel = `input[type=radio][name=${this.name}]:checked`;
            return this.form.find(sel).val();
        }
    }

}