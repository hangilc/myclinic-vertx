
export async function modalOpen(ele, closeHandler){
    return new Promise(resolve => {
        let backdrop = document.createElement("div");
        backdrop.classList.add("modal-dialog-backdrop");
        ele.classList.add("modal-dialog-content");
        document.body.append(ele);
        ele.style.display = "block";
        document.body.append(backdrop);
        let close = retVal => {
            backdrop.remove();
            ele.style.display = "none";
            ele.remove();
            resolve(retVal);
        };
        closeHandler(close);
    });
}
