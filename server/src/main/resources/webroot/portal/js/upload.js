export function uploadFile(path, files, fileNameToStoreNameConv = null, progress = null,
                           additionalFormDataParams = null) {
    return new Promise((resolve, reject) => {
        if (fileNameToStoreNameConv == null) {
            fileNameToStoreNameConv = (name, index) => name;
        }
        if (progress == null) {
            progress = pct => {
            };
        }
        if (additionalFormDataParams == null) {
            additionalFormDataParams = {};
        }
        let method = "POST";
        let xhr = new XMLHttpRequest();
        let url = path;
        let formData = new FormData();
        for (let key of Object.keys(additionalFormDataParams)) {
            formData.append(key, additionalFormDataParams[key]);
        }
        let index = 1;
        for (let file of files) {
            let filename = fileNameToStoreNameConv(file.name, index);
            formData.append(`file{$index}`, file, filename);
            index += 1;
        }

        xhr.onload = e => {
            let contentType = xhr.getResponseHeader("Content-Type");
            if (contentType.startsWith("application/json")) {
                resolve(JSON.parse(xhr.responseText));
            } else {
                resolve(xhr.responseText);
            }
        };
        xhr.onerror = e => {
            reject(xhr.statusText + ": " + xhr.responseText);
        };
        xhr.upload.onprogress = e => {
            if (e.lengthComputable) {
                let pct = Math.round((e.loaded / e.total) * 100);
                progress(pct);
            }
        };
        xhr.open(method, url);
        xhr.send(formData);
    });
}

export class FileUploader {
    constructor(url, file) {
        this.url = url;
        this.file = file;
        this.filename = null;
        this.progress = pct => {
        };
        this.attr = {};
    }

    setFilename(filename) {
        this.filename = filename;
    }

    setProgressHandler(handler) {
        this.progress = handler;
    }

    setAttr(key, value) {
        this.attr[key] = value;
    }

    getLabel(){
        return this.filename || this.file.name;
    }

    upload() {
        let xhr = new XMLHttpRequest();
        let formData = new FormData();
        for (let key of Object.keys(this.attr)) {
            formData.append(key, this.attr[key]);
        }
        let fileKey = "file1";
        if( fileKey in this.attr ){
            throw new Error("Attribute 'file1' is already present.");
        }
        if( this.filename ){
            formData.append(fileKey, this.file, this.filename);
        } else {
            formData.append(fileKey, this.file);
        }
        let promise = new Promise((resolve, reject) => {
            xhr.onload = e => {
                let contentType = xhr.getResponseHeader("Content-Type");
                if (contentType.startsWith("application/json")) {
                    resolve(JSON.parse(xhr.responseText));
                } else {
                    resolve(xhr.responseText);
                }
            };
            xhr.onerror = e => {
                reject(xhr.statusText + ": " + xhr.responseText);
            };
            xhr.upload.onprogress = e => {
                if (e.lengthComputable) {
                    let pct = Math.round((e.loaded / e.total) * 100);
                    this.progress(pct);
                }
            };
            xhr.open("POST", this.url);
            xhr.send(formData);
        });
        return {
            promise,
            abort: () => xhr.abort,
            xhr
        };
    }
}
