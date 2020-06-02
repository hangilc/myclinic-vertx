"use strict";

// TODO: implement text_vertically_justified

(function(exports){

exports.drawerToSvg = function(ops, options){
    options = options || {};

    // var attr = {};
    // ['width', 'height'].forEach(function(key){
    //     if( key in options ){
    //         attr[key] = options[key];
    //     }
    // })

    var ns = "http://www.w3.org/2000/svg";
    var pen_color = "rgb(0,0,0)";
    var pen_width = "1px";
    var pen_dict = {};
    var font_name, font_size, font_weight, font_italic;
    var font_dict = {};
    var text_color = "rgb(0,0,0)";
    var i, n, op;
    var curr_x, curr_y;

    var svg = document.createElementNS(ns, "svg");
    ['width', 'height', "viewBox"].forEach(function(key){
        if( key in options ){
            svg.setAttributeNS(null, key, options[key]);
        }
    })
    
    function draw_move_to(x, y){
        curr_x = x;
        curr_y = y;
    }

    function draw_line_to(x, y){
    	var e = document.createElementNS(ns, "line");
    	e.setAttributeNS(null, "x1", curr_x);
    	e.setAttributeNS(null, "y1", curr_y);
    	e.setAttributeNS(null, "x2", x);
    	e.setAttributeNS(null, "y2", y);
    	e.setAttributeNS(null, "style", "stroke:" + pen_color + ";stroke-width:" + pen_width);
        curr_x = x;
        curr_y = y;
        return e;
    }

    function create_pen(name, r, g, b, width){
        var color = "rgb(" + r + "," + g + "," + b + ")";
        pen_dict[name] = {width: width + "px", color: color};
    }

    function set_pen(name){
        var pen = pen_dict[name];
        pen_color = pen.color;
        pen_width = pen.width;
    }

    function mmToPixel(dpi, mm){
        var inch = mm/25.4;
        return Math.floor(dpi * inch);
    };
    
    function pixelToMm(dpi, px){
        var inch = px / dpi;
        return inch * 25.4;
    }

    function create_font(name, font_name, size, weight, italic){
        font_dict[name] = {font_name: font_name, font_size: size, font_weight: weight, font_italic: italic};
    }

    function set_font(name){
        var font = font_dict[name];
        font_name = font.font_name;
        font_size = font.font_size;
        font_weight = font.font_weight;
        font_italic = font.font_italic;
    }

    function set_text_color(r, g, b){
        text_color = "rgb(" + r + "," + g + "," + b + ")";
    }

    function draw_chars(chars, xs, ys){
        var e = document.createElementNS(ns, "text");
        var attrs = {
            fill: text_color,
            "font-family": font_name,
            "font-size": font_size,
            "font-weight": font_weight ? "bold" : "normal",
            "font-italic": font_italic ? "italic": "normal",
            "text-anchor": "start",
            //'dominant-baseline': "text-after-edge",
            "dy": "1em"
        };
        for(var key in attrs){
        	e.setAttributeNS(null, key, attrs[key]);
        }
        if( typeof xs === "number" || xs instanceof Number ){
        	e.setAttributeNS(null, "x", xs);
        } else {
            e.setAttributeNS(null, "x", xs.join(","));
        }
        if( typeof ys === "number" || ys instanceof Number){
        	e.setAttributeNS(null, "y", ys);
        } else {
        	e.setAttributeNS(null, "y", ys.join(","));
        }
        e.appendChild(document.createTextNode(chars));
        return e;
    }

    for(i=0,n=ops.length;i<n;i++){
        op = ops[i];
        switch(op[0]){
            case "move_to":
                draw_move_to(op[1], op[2]);
                break;
            case "line_to":
                svg.appendChild(draw_line_to(op[1], op[2]));
                break;
            case "create_pen":
                create_pen(op[1], op[2], op[3], op[4], op[5]);
                break;
            case "set_pen":
                set_pen(op[1]);
                break;
            case "create_font":
                create_font(op[1], op[2], op[3], op[4], op[5]);
                break;
            case "set_font":
                set_font(op[1]);
                break;
            case "set_text_color":
                set_text_color(op[1], op[2], op[3]);
                break;
            case "draw_chars":
                svg.appendChild(draw_chars(op[1], op[2], op[3]));
                break;
            default:
                throw new Error("unknown drawer op:", op);
                break;
        }
    }
    return svg;	
}
})(typeof exports === "undefined" ? window : exports);