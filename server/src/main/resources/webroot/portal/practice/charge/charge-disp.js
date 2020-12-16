import {Component} from "../component.js";
import {createElementFrom} from "../../../js/create-element-from.js";

let tmpl = `
<div></div>
`;

export class ChargeDisp{

    constructor(charge){
        this.ele = createElementFrom(tmpl);
        if( charge ){
            let value = +(charge.charge);
            this.ele.innerText = `請求額：${value.toLocaleString()}円`;
        } else {
            this.ele.innerText = "［未請求］";
        }
    }

}

// export class ChargeDispOrig extends Component {
//     constructor(ele, map, rest) {
//         super(ele, map, rest);
//     }
//
//     init(charge){
//         if( charge ){
//             let value = +(charge.charge);
//             this.ele.text(`請求額：${value.toLocaleString()}円`);
//         } else {
//             this.ele.text("［未請求］");
//         }
//     }
// }