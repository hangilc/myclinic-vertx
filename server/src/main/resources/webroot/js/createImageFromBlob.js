
export function createImageFromBlob(buf){
    let img = document.createElement("img");
    img.src = URL.createObjectURL(new Blob([buf], {type: "image/jpeg"}));
    return img;
}
