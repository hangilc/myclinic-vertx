
export function modalOpen(ele, closeHandler){
    let backdrop = document.createElement("div");
    backdrop.classList.add("modal-dialog-backdrop");
    ele.classList.add("modal-dialog-content");
    document.body.append(ele);
    ele.style.display = "block";
    document.body.append(backdrop);
    closeHandler(() => {
        backdrop.remove();
        ele.style.display = "none";
        ele.remove();
    });
}
