export * from "../js/parse-element.js";

// function hyphenToCamel(s) {
//     let ps = s.split(/-+/);
//     for (let i = 1; i < ps.length; i++) {
//         let p = ps[i];
//         ps[i] = p[0].toUpperCase() + p.substring(1);
//     }
//     return ps.join("");
// }
//
// function probeXClass(e) {
//     let attrClass = e.attr("class");
//     if (attrClass) {
//         for (let cls of attrClass.split(/\s+/)) {
//             if (cls.startsWith("x-")) {
//                 e.removeClass(cls);
//                 return hyphenToCamel(cls.substring(2));
//             }
//         }
//     }
//     return null;
// }
//
// function parseElementIter(ele, map) {
//     ele = $(ele);
//     if (ele.length === 1) {
//         let name = probeXClass(ele);
//         if (name) {
//             map[name] = ele;
//             if (name.endsWith("_")) {
//                 name = name.substring(0, name.length - 1);
//                 let submap = {};
//                 parseElementIter(ele.children(), submap);
//                 map[name] = submap;
//             } else {
//                 parseElementIter(ele.children(), map);
//             }
//         } else {
//             parseElementIter(ele.children(), map);
//         }
//     } else {
//         for (let e of ele.toArray()) {
//             parseElementIter(e, map);
//         }
//     }
// }
//
// export function parseElement(ele) {
//     let map = {};
//     parseElementIter(ele, map);
//     return map;
// }
//
