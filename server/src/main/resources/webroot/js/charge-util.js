
export function chargeRep(charge){
    if( charge ){
        let value = +(charge.charge);
        return `請求額：${value.toLocaleString()}円`;
    } else {
        return "［未請求］";
    }
}