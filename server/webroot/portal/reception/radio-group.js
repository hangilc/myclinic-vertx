export class RadioGroup {
    constructor(form, name) {
        this.form = form;
        this.name = name;
    }

    get(){
        return this.form.querySelector(`input[type=radio][name='${this.name}']:checked`).value;
    }

    set(value){
        this.form.querySelector(`input[type=radio][name='${this.name}'][value='${value}']`).checked = true;
    }
}