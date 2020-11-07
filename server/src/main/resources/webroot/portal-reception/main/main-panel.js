
let tmpl = `
MAIN
`;

export class MainPanel {
    constructor(ele, rest){
        ele.innerHTML = tmpl;
        this.rest = rest;
    }
}