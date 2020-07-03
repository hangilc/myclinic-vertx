
export class Broadcaster {
    constructor(){
        this.topicListenerMap = {};
    }

    listen(topic, cb){
        let listeners = this.topicListenerMap[topic];
        if( listeners == null ){
            listeners = this.topicListenerMap[topic] = [];
        }
        listeners.push(cb);
    }

    broadcast(topic, value){
        let listeners = this.topicListenerMap[topic];
        if( listeners ){
            for(let listener of listeners){
                listener(value);
            }
        }
    }
}