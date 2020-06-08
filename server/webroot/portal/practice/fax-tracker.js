import {Component} from "./component.js";

export class FaxTracker extends Component {
    constructor(ele, map, rest) {
        super(ele, map, rest);
        this.messageElement = map.element;
        this.closeElement = map.close;
    }

    init(location){
        let socket = new WebSocket(location);
        socket.addEventListener("message", event => {
            console.log("event", event.data);
        })
        this.closeElement.on("click", event => {
            socket.close();
            this.ele.trigger("closed");
        })
    }

    onClosed(cb){
        this.ele.on("closed", cb);
    }
}