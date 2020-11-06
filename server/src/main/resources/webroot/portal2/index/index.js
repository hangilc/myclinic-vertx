import {createPractice} from "../practice/practice.js";

export class Index {
    constructor(rest, sidebarUl, main){
        this.rest = rest;
        this.sidebarUl = sidebarUl;
        this.main = main;
        sidebarUl.append(this.createLink("診察", () => {
            let ele = createPractice(rest);
            main.innerHTML = "";
            main.append(ele);
        }));
    }

    async init(){

    }

    createLink(label, action){
        let li = document.createElement("li");
        let a = document.createElement("a");
        a.href = "javascript:void(0)";
        a.innerText = label;
        a.onclick = action;
        li.append(a);
        return li;
    }

}