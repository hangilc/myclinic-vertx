export class HttpClient {
    constructor(url){
        if( !url ){
            url = "";
        } else {
            url = url.replace(/\/$/, "");
        }
        this.url = url;
    }

    async uploadFileBlob(path, fileBits, filename, attr = null, method = "POST"){
        let file = new File(fileBits, filename);
        return new Promise((resolve, reject) => {
            let formData = new FormData();
            if( attr ){
                for(let key of Object.keys(attr)){
                    formData.append(key, attr[key]);
                }
            }
            formData.append("file", file);
            let xhr = new XMLHttpRequest();
            let url = this.url + path;
            xhr.onload = e => {
                if( xhr.status === 200 ){
                    resolve(xhr.response);
                } else {
                    reject(xhr.statusText + ": " + xhr.responseText);
                }
            }
            xhr.onerror = e => {
                reject(xhr.statusText + ": " + xhr.responseText);
            }
            xhr.open(method, url);
            xhr.send(formData);
        });
    }

    REQUEST(method, path, params, body){
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            let url = this.url + path;
            if( params && Object.keys(params).length !== 0 ){
                let parts = [];
                for(let key of Object.keys(params) ){
                    let val = params[key];
                    parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(val));
                }
                url += "?" + parts.join("&");
            }
            xhr.onload = e => {
                let contentType = xhr.getResponseHeader("Content-Type");
                if( contentType.startsWith("application/json") ){
                    resolve(JSON.parse(xhr.responseText));
                } else {
                    resolve(xhr.responseText);
                }
            }
            xhr.onerror = e => {
                reject(xhr.statusText + ": " + xhr.responseText);
            }
            xhr.open(method, url);
            xhr.send(body);
        });
    }

    GET(path, params){
        return this.REQUEST("GET", path, params, "");
    }

    POST(path, body, params){
        return this.REQUEST("POST", path, params, JSON.stringify(body));
    }

    PUT(path, body, params){
        return this.REQUEST("PUT", path, params, JSON.stringify(body));
    }

    DELETE(path, params){
        return this.REQUEST("DELETE", path, params, "");
    }
}