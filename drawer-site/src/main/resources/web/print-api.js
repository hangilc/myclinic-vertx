export class PrintAPI {
    constructor(url){
        if( !url ){
            url = "";
        } else {
            url = url.replace(/\/$/, "");
        }
        this.url = url;
    }

    async listSetting(){
        return await this.GET("/setting/", {});
    }

    async createSetting(name){
        return await this.POST("/setting/" + name, "", {});
    }

    async deleteSetting(name){
        return await this.DELETE("/setting/" + name, {});
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

    async updateAuxSetting(name, auxSetting){
        return await this.PUT(`/setting/${name}/aux`, auxSetting, {});
    }

    async printDialog(name){
        if( !name ){
            name = "";
        }
        return await this.GET("/print-dialog/" + name, {});
    }

    async print(setup, pages, setting){
        if( !setting ){
            setting = "";
        }
        let req = { setup, pages };
        return await this.POST("/print/" + setting, req);
    }

    async getPref(key){
        return await this.GET(`/pref/${key}`);
    }

    async setPref(key, value){
        return await this.POST(`/pref/${key}`, value, {});
    }

    async deletePref(key){
        return await this.DELETE(`/pref/${key}`, {});
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