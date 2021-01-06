
class Success {
    constructor(value){
        this.value = value;
    }

    isSuccess(){
        return true;
    }

    isFailure(){
        return false;
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

    isFailure(){
        return true;
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

export function throwError(result){
    if( result.isSuccess() ){
        return result.getValue();
    } else {
        throw new Error(result.getMessage());
    }
}

export function alertAndReturn(value){
    return result => {
        if( result.isSuccess() ){
            return result.getValue();
        } else {
            alert(result.getMessage());
            return value;
        }
    }
}

export let alertAndReturnNull = alertAndReturn(null);
export let alertAndReturnUndefined = alertAndReturn(undefined);
