export function success(value){
    return {
        ok: true,
        value
    };
}

export function error(message){
    return {
        ok: false,
        message
    };
}

