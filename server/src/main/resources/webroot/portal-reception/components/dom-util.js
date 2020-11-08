
export function getRadioValue(form, name){
    return form.querySelector(`input[type=radio][name='${name}']:checked`).value;
}

export function setRadioValue(form, name, value){
    form.querySelector(`input[type=radio][name='${name}'][value='${value}']`).checked = true;
}
