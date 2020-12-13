export function extendProp(prop, values=null){
    if( values == null ){
        values = {};
    }
    return Object.assign(Object.create(prop), values);
}