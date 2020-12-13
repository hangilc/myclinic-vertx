
export function parseQueryParams(){
    let query = window.location.search;
    if( !query ){
        return {};
    }
    let params = {};
    query = query.replace(/^\?/, "");
    let parts = query.split(/&/);
    for(let part of parts){
        let [k, v] = part.split(/=/);
        params[k] = v;
    }
    return params;
}