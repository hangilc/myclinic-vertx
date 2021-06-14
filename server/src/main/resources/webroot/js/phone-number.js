export function detectPhoneNumber(src){
    let start = 0;
    const result = [];
    let iter = 0;
    const iterMax = 6;
    while(start < src.length){
        if( handle1() || handle2() ){
            const last = result[result.length-1];
            start = last.index + last.length;
        } else {
            break;
        }
        if( iter++ >= iterMax ){
            break;
        }
    }
    return result;

    function addResult(index, length, phoneNumber){
        result.push({index, length, phone: phoneNumber});
    }

    function mkPhoneNumber3(a, b, c){
        a = a.replace(/^0/, "");
        return `+81${a}${b}${c}`;
    }

    function handle1(){
        const pat = /(\d+)-(\d+)-(\d+)/g;
        pat.lastIndex = start;
        const m = pat.exec(src);
        if( m ){
            addResult(m.index, m[0].length, mkPhoneNumber3(m[1], m[2], m[3]));
            return true;
        } else {
            return false;
        }
    }

    function handle2(){
        const pat = /(\d+)\s+(\d+)\s+(\d+)/g;
        pat.lastIndex = start;
        const m = pat.exec(src);
        if( m ){
            addResult(m.index, m[0].length, mkPhoneNumber3(m[1], m[2], m[3]));
            return true;
        } else {
            return false;
        }
    }
}