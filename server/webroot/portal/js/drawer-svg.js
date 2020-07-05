export function drawerToSvg(ops, options){
    options = options || {};

    let ns = "http://www.w3.org/2000/svg";
    let pen_color = "rgb(0,0,0)";
    let pen_width = "1px";
    let pen_style = [];
    let pen_dict = {};
    let font_name, font_size, font_weight, font_italic;
    let font_dict = {};
    let text_color = "rgb(0,0,0)";
    let i, n, op;
    let curr_x, curr_y;

    let svg = document.createElementNS(ns, "svg");
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
    	let e = document.createElementNS(ns, "line");
    	e.setAttributeNS(null, "x1", curr_x);
    	e.setAttributeNS(null, "y1", curr_y);
    	e.setAttributeNS(null, "x2", x);
    	e.setAttributeNS(null, "y2", y);
    	//e.setAttributeNS(null, "style", "stroke:" + pen_color + ";stroke-width:" + pen_width);
        e.setAttributeNS(null, "stroke", pen_color);
        e.setAttributeNS(null, "stroke-width", pen_width);
        if( pen_style.length > 0 ){
            e.setAttributeNS(null, "stroke-dasharray", pen_style);
        }
        curr_x = x;
        curr_y = y;
        return e;
    }

    function create_pen(name, r, g, b, width, penstyle){
        let color = "rgb(" + r + "," + g + "," + b + ")";
        penstyle = penstyle.join(" ");
        pen_dict[name] = {width: width + "px", color: color, penstyle: penstyle};
    }

    function set_pen(name){
        let pen = pen_dict[name];
        pen_color = pen.color;
        pen_width = pen.width;
        pen_style = pen.penstyle;
    }

    function mmToPixel(dpi, mm){
        let inch = mm/25.4;
        return Math.floor(dpi * inch);
    }
    
    function pixelToMm(dpi, px){
        let inch = px / dpi;
        return inch * 25.4;
    }

    function create_font(name, font_name, size, weight, italic){
        font_dict[name] = {font_name: font_name, font_size: size, font_weight: weight, font_italic: italic};
    }

    function set_font(name){
        let font = font_dict[name];
        font_name = font.font_name;
        font_size = font.font_size;
        font_weight = font.font_weight;
        font_italic = font.font_italic;
    }

    function set_text_color(r, g, b){
        text_color = "rgb(" + r + "," + g + "," + b + ")";
    }

    function draw_chars(chars, xs, ys){
        let e = document.createElementNS(ns, "text");
        let attrs = {
            fill: text_color,
            "font-family": font_name,
            "font-size": font_size,
            "font-weight": font_weight ? "bold" : "normal",
            "font-italic": font_italic ? "italic": "normal",
            "text-anchor": "start",
            //'dominant-baseline': "text-after-edge",
            "dy": "1em"
        };
        for(let key in attrs){
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
                create_pen(op[1], op[2], op[3], op[4], op[5], op[6]);
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
                // TODO: circle
            default:
                throw new Error("unknown drawer op: " + op);
        }
    }
    return svg;	
}