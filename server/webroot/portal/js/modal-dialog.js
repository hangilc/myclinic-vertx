
export function modalOpen(ele, closeHandler){
    return new Promise(resolve => {
        let backdrop = document.createElement("div");
        backdrop.classList.add("modal-dialog-backdrop");
        ele.classList.add("modal-dialog-content");
        ele.style.display = "block";
        document.body.append(backdrop);
        document.body.append(ele);
        let close = retVal => {
            backdrop.remove();
            ele.style.display = "none";
            ele.remove();
            console.log("returning", retVal);
            resolve(retVal);
        };
        closeHandler(close);
    });
}
