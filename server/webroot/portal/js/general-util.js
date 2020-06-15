
export function compare(a, b){
    if( a > b ){
        return 1;
    } else if( a < b ) {
        return -1;
    } else {
        return 0;
    }
}

export function compareBy(...props){
    return (a, b) => {
        for(let p of props){
            if( p.startsWith("-") ){
                p = p.substring(1);
                let pa = a[p];
                let pb = b[p];
                let c = -compare(pa, pb);
                if( c !== 0 ){
                    return c;
                }
            } else {
                let pa = a[p];
                let pb = b[p];
                let c = compare(pa, pb);
                if( c !== 0 ){
                    return c;
                }
            }
        }
        return 0;
    };
}
