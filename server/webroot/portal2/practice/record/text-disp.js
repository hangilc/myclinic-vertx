
export function createTextDisp(content){
    let ele = document.createElement("div");
    ele.classList.add("text-disp");
    ele.innerText = content;
    ele.onclick = event => ele.dispatchEvent(new Event("do-edit", {bubbles: true}));
    return ele;
}