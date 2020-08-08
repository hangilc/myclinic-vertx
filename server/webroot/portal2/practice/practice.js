
let tmpl = `
<h2>診察</h2>
<div>
    <a href="javascript:void(0)">患者選択▼</a>
</div>
`;

export function createPractice(rest){
    let ele = document.createElement("div");
    ele.innerHTML = tmpl;
    return ele;
}