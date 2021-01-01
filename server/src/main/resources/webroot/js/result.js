
class Success {
    constructor(value){
        this.value = value;
    }

    isSuccess(){
        return true;
    }

    getValue(){
        return this.value;
    }
}

class Failure {
    constructor(message){
        this.message = message;
    }

    isSuccess(){
        return false;
    }

    getMessage(){
        return this.message;
    }
}

export function success(value){
    return new Success(value);
}

export function failure(message){
    return new Failure(message);
}