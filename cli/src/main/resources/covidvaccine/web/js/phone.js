
export class Phone {
    constructor(tokenSupplier){
        this.device = null;
        this.tokenSupplier = tokenSupplier;
    }

    async callout(phoneNumber){
        await this.ensureDevice();
        if( this.device != null ){
            // noinspection FallThroughInSwitchStatementJS
            switch(this.device.status()){
                case "offline": {
                    await this.initDevice();
                    // fall through
                }
                case "ready": {
                    return this.device.connect({
                        phone: phoneNumber
                    });
                }
                case "busy": {
                    alert("Device is busy.");
                    return null;
                }
                default: {
                    console.log("Unknown device status:", this.device.status());
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    getConnections(){
        if( this.device !== null ){
            return this.device.getConnections();
        } else {
            return [];
        }
    }

    disconnectAll(){
        if( this.device !== null ){
            return this.device.disconnectAll();
        } else {
            return [];
        }
    }

    async initDevice(){
        const device = this.device;
        let token = await this.tokenSupplier();
        device.setup(token, {
            "edge": "tokyo"
        });
        device.on("incoming", c => {
            console.log("incoming", c);
            c.accept();
        });
        return new Promise((accept, reject) => {
            device.on("ready", _ => accept(true));
            console.log(device);
        });
    }

    async ensureDevice(){
        if( this.device == null ){
            this.device = new Twilio.Device();
            return await this.initDevice();
        } else {
            return true;
        }
    }
}

// const tmpl = `
//     <div class="form-inline">
//         <input type="text" class="x-phone-input form-control mr-2"/>
//         <button class="x-callout-button btn btn-primary">発信</button>
//     </div>
// `;

// export class CalloutDialog extends Dialog {
//     constructor(number, rest){
//         super();
//         this.number = number;
//         this.rest = rest;
//         this.setTitle("電話発信");
//         this.getBody().innerHTML = tmpl;
//         this.bodyMap = parseElement(this.getBody());
//         this.bodyMap.calloutButton.addEventListener("click", async e => await this.doCallout());
//     }
//
//     async doCallout(){
//         if( device == null ){
//             await this.setupDevice();
//         }
//         const number = this.bodyMap.phoneInput.value;
//         if( number ){
//             device.connect({
//                 phone: number
//             });
//         }
//     }
//
//     async setupDevice(){
//         let token = await this.rest.twilioWebphoneToken();
//         device = new Twilio.Device();
//         device.setup(token, {
//             "edge": "tokyo"
//         });
//     }
// }