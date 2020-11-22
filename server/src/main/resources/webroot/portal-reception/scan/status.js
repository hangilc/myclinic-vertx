export let PREPARING = "preparing";
export let SCANNING = "scanning";
export let UPLOADING = "uploading";
export let PARTIALLY_UPLOADED = "partially-uploaded";
export let UPLOADED = "uploaded";

export let changeMap = {
    [PREPARING]: [SCANNING, UPLOADING],
    [SCANNING]: [PREPARING],
    [UPLOADING]: [UPLOADED, PARTIALLY_UPLOADED],
    [PARTIALLY_UPLOADED]: [UPLOADED, PREPARING],
    [UPLOADED]: []
};
