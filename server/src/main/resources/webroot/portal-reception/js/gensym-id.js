var nextId = 1;

function getNextId(){
    return nextId++;
}

export function gensymId(ele){
    let map = {};
    ele.querySelectorAll("[id^='gensym-']").forEach(e => {
        let id = e.id;
        let newId = id.replace(/^gensym-/, `GENSYM-${getNextId()}-`);
        map[id] = newId;
        e.id = newId;
    });
    console.log(map);
    ele.querySelectorAll("label[for^='gensym-']").forEach(e => {
        console.log(e);
        let id = e.getAttribute("for");
        console.log(id);
        let newId = map[id];
        if( newId ){
            e.setAttribute("for", newId);
        }
    })
}