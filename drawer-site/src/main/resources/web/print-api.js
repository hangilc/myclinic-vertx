export class PrintAPI {
    constructor(){
        this.url = "http://127.0.0.1:48080";
    }

    async listSetting(){
        return await this.GET("/setting/", {});
    }

    async createSetting(name){
        return await this.POST("/setting/" + name, "", {});
    }

    async getSetting(name){
        return await this.GET("/setting/" + name, {});
    }

    async getSettingDetail(name){
        return await this.GET(`/setting/${name}/detail`, {});
    }

    async updateSetting(name, setting){
        return await this.PUT(`/setting/${name}`, setting, {});
    }

    async printDialog(name){
        if( !name ){
            name = "";
        }
        return await this.GET("/print-dialog/" + name, {});
    }

    async print(setup, pages, setting){
        let req = { setup, pages };
        return await this.POST("/print/" + setting, req);
    }

    REQUEST(method, path, params, body){
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            let url = this.url + path;
            if( params && Object.keys(params).length !== 0 ){
                let parts = [];
                for(let key in Object.keys(params) ){
                    let val = params[key];
                    parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(val));
                }
                url += "?" + parts.join("&");
            }
            xhr.onload = e => {
                let contentType = xhr.getResponseHeader("Content-Type");
                console.log("content-type", contentType);
                if( contentType.startsWith("application/json") ){
                    console.log("JSON.parse");
                    console.log(xhr.responseText);
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
        console.log("PUT", typeof body);
        return this.REQUEST("PUT", path, params, JSON.stringify(body));
    }
}