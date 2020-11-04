import {ModalDialog} from "./modal-dialog.js";
import {parseElement} from "./parse-node.js";

let tmpl = `
<div>
    名前：<span class="x-name"></span>
</div>
<div>
    dx: <input name="dx" class="x-dx" size="6"/> mm
</div>
<div>
    dy: <input name="dy" class="x-dy" size="6"/> mm
</div>
<div>
    scale: <input name="scale" class="x-scale" size="6"/> mm
</div>
`;

let commandsTmpl = `
    <button class="x-enter">入力</button>
    <button class="x-cancel">キャンセル</button>
`;

export async function openEditAuxSettingDialog(name, auxSetting, api){
    let dialog = new ModalDialog("移動・縮小の変更");
    dialog.getContent().innerHTML = tmpl;
    let map = parseElement(dialog.getContent());
    map.name.innerText = name;
    map.dx.value = auxSetting.dx;
    map.dy.value = auxSetting.dy;
    map.scale.value = auxSetting.scale;
    dialog.getCommands().innerHTML = commandsTmpl;
    let cmap = parseElement(dialog.getCommands());
    cmap.enter.addEventListener("click", async event => await doEnter(dialog, name, map, api));
    cmap.cancel.addEventListener("click", event => dialog.close(false));
    return await dialog.open();
}

async function doEnter(dialog, name, map, api){
    let dx = getNumberValue(map.dx);
    if( dx === false ){
        return;
    }
    let dy = getNumberValue(map.dy);
    if( dy === false ){
        return;
    }
    let scale = getNumberValue(map.scale);
    if( scale === false ){
        return;
    }
    let aux = {dx, dy, scale};
    console.log(aux);
    await api.updateAuxSetting(name, aux);
    dialog.close(true);
}

function getNumberValue(input){
    let value = input.value;
    if( value === "" ){
        alert(`${input.name} の値が入力されていません。`);
        return false;
    }
    let f = parseFloat(value);
    if( isNaN(f) ){
        alert(`${input.name} の入力が不適切です。`);
        return false;
    }
    return f;
}