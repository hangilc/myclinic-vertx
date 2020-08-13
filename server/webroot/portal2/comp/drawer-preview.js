import {drawerToSvg} from "./drawer-svg.js";

export function createDrawerPreview(ops, scale=1.0){
    let width = 148;
    let height = 210;
    let vbWidth = width;
    let vbHeight = height;
    if( scale !== 1.0 ){
        vbWidth /= scale;
        vbHeight /= scale;
    }
    let options = {
        width: `${width}mm`,
        height: `${height}mm`,
        viewBox: `0 0 ${vbWidth} ${vbHeight}`
    }
    return drawerToSvg(ops, options);
}