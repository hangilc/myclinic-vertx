import {drawerToSvg} from "../../js/drawer-svg.js";

export class DrawerDisp {
    constructor(ops, width, height, viewBox){
        this.ele = drawerToSvg(ops, {width, height, viewBox});
    }
}