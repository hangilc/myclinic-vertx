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

    async listScannerDevices(){
        return await this.GET("/scanner/device/", {});
    }

    async scan(deviceId = null, progress = null, resolution = null){
        return new Promise((resolve, reject) => {
            if( progress == null ){
                progress = pct => {};
            }
            let xhr = new XMLHttpRequest();
            let url = this.url + "/scanner/scan";
            let hasParam = false;
            if( resolution ){
                if( !hasParam ){
                    url += "?";
                    hasParam = true;
                }
                let resolutionParam = encodeURIComponent(resolution);
                url += `resolution=${resolutionParam}`;
            }
            xhr.responseType = "";
            xhr.onprogress = event => {
                if( event.total !== 0 ){
                    let pct = Math.round((event.loaded / event.total) * 100);
                    progress(pct);
                }
            };
            xhr.onload = event => {
                let filename = xhr.getResponseHeader("x-saved-image");
                resolve(filename);
            };
            xhr.onerror = event => {
                reject(xhr.statusText + ": " + xhr.responseText);
            };
            xhr.open("GET", url);
            xhr.send();
        });
    }

    async getScannedImage(name){
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            let url = this.url + `/scanner/image/${name}`;
            xhr.responseType = "arraybuffer";
            xhr.onload = event => {
                resolve(xhr.response);
            };
            xhr.onerror = event => {
                reject(xhr.statusText + ": " + xhr.responseText);
            };
            xhr.open("GET", url);
            xhr.send();
        });
    }

    async deleteScannedFile(name){
        return await this.DELETE(`/scanner/image/${name}`, {});
    }

    async beep(){
        return await this.GET("/beep", {});
    }

    async createUploadJob(job){  // job: dev.myclinic.vertx.drawersite.UploadJob
        return await this.POST("/upload-job", job);
    }

    async deleteUploadJob(jobName){
        return await this.DELETE("/upload-job/" + jobName);
    }

    async listUploadJob(){
        return await this.GET("/upload-job/");
    }

    UPLOAD(path, files, fileNameToStoreNameConv = null, progress = null,
           additionalFormDataParams = null){
        return new Promise((resolve, reject) => {
            if( fileNameToStoreNameConv == null ){
                fileNameToStoreNameConv = (name, index) => name;
            }
            if( progress == null ){
                progress = pct => {};
            }
            if( additionalFormDataParams == null ){
                additionalFormDataParams = {};
            }
            let method = "POST";
            let xhr = new XMLHttpRequest();
            let url = this.url + path;
            let formData = new FormData();
            for(let key of Object.keys(additionalFormDataParams)){
                formData.append(key, additionalFormDataParams[key]);
            }
            let index = 1;
            for(let file of files){
                let filename = fileNameToStoreNameConv(file.name, index);
                formData.append(`file{$index}`, file, filename);
                index += 1;
            }

            xhr.onload = e => {
                let contentType = xhr.getResponseHeader("Content-Type");
                if( contentType.startsWith("application/json") ){
                    resolve(JSON.parse(xhr.responseText));
                } else {
                    resolve(xhr.responseText);
                }
            };
            xhr.onerror = e => {
                reject(xhr.statusText + ": " + xhr.responseText);
            };
            xhr.upload.onprogress = e => {
                if( e.lengthComputable ){
                    let pct = Math.round((e.loaded / e.total) * 100);
                    progress(pct);
                }
            };
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