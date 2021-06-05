import {Dialog} from "./dialog2.js";
import {parseElement} from "./parse-node.js";
import {createElementFrom} from "./create-element-from.js";

let rest = null;
let device = null;

export async function initPhone(restObject){
    rest = restObject;
    //await setupDevice();
}

async function setupDevice(cb){
    if( device == null ){
        let token = await rest.twilioWebphoneToken();
        device = new Twilio.Device();
        device.setup(token, {
            "edge": "tokyo"
        });
    } else {

    }
}

export async function callout(phoneNumber){
    await setupDevice();
    if( device != null ){
        device.connect({
            phone: phoneNumber
        });
    }
}

const tmpl = `
    <div class="form-inline">
        <input type="text" class="x-phone-input form-control mr-2"/>
        <button class="x-callout-button btn btn-primary">発信</button>
    </div>
`;

export class CalloutDialog extends Dialog {
    constructor(number, rest){
        super();
        this.number = number;
        this.rest = rest;
        this.setTitle("電話発信");
        this.getBody().innerHTML = tmpl;
        this.bodyMap = parseElement(this.getBody());
        this.bodyMap.calloutButton.addEventListener("click", async e => await this.doCallout());
    }

    async doCallout(){
        if( device == null ){
            await this.setupDevice();
        }
        const number = this.bodyMap.phoneInput.value;
        if( number ){
            device.connect({
                phone: number
            });
        }
    }

    async setupDevice(){
        let token = await this.rest.twilioWebphoneToken();
        device = new Twilio.Device();
        device.setup(token, {
            "edge": "tokyo"
        });
    }
}