import {drawerToSvg} from "./drawer-svg.js";

// paperWidth (mm), paperHeight (mm)
// A5: 148 210
// A4: 210 297
// B5: 182 257
export function createDrawerPreview(ops, paperWidth, paperHeight, scale=1.0){
    let options = {
        width: `${paperWidth * scale}mm`,
        height: `${paperHeight * scale}mm`,
        viewBox: `0 0 ${paperWidth} ${paperHeight}`
    }
    return drawerToSvg(ops, options);
}