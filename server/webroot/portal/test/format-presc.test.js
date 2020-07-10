import * as fmt from "../js/format-presc.js";

describe("parsePresc", function () {
    it("should return empty result", function () {
        let result = fmt.formatPresc("");
        chai.expect(result).to.equal("");
    });
    it("should return same single item", function () {
        let src = "１）オメプラール（２０ｍｇ）　１錠";
        let dst = fmt.formatPresc(src);
        let ok = "１）オメプラール（２０ｍｇ）　　　　　　　１錠";
        chai.expect(dst).to.equal(ok);
    });
    it("should handle single full naifuku item", function () {
        let src = "１）アムロジピン錠２．５ｍｇ　１錠\n" +
            "　　分３　毎食後　３０日分";
        let ok = "１）アムロジピン錠２．５ｍｇ　　　　　　　１錠\n" +
            "　　分３　毎食後　　　　　　　　　　　　　３０日分";
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });
    it("should handle naifuku and gaiyou", function () {
        let src = "１）アムロジピン錠２．５ｍｇ　１錠\n" +
            "　　分３　毎食後　３０日分\n" +
            "２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚\n" +
            "　　１日２回患部に貼付";
        let ok = "１）アムロジピン錠２．５ｍｇ　　　　　　　１錠\n" +
            "　　分３　毎食後　　　　　　　　　　　　　３０日分\n" +
            "２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚\n" +
            "　　１日２回患部に貼付";
        console.log(ok);
        console.log(fmt.formatPresc(src));
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });
    it("should handle prefix", function () {
        let src = `院外処方
                -Ｒｐ）
                -１）アムロジピン錠２．５ｍｇ　１錠
                -　　分３　毎食後　３０日分
                -２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付`;
        let ok = `院外処方
                -Ｒｐ）
                -１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
                -　　分３　毎食後　　　　　　　　　　　　　３０日分
                -２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付`;
        src = skipPre(src);
        ok = skipPre(ok);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

    it("should handle control", function () {
        let src = `院外処方
                -Ｒｐ）
                -１）アムロジピン錠２．５ｍｇ　１錠
                -　　分３　毎食後　３０日分
                -２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付
                -@0410対応
                -@有効期限：2020-07-10`;
        let ok = `院外処方
                -Ｒｐ）
                -１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
                -　　分３　毎食後　　　　　　　　　　　　　３０日分
                -２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付
                -@0410対応
                -@有効期限：2020-07-10`;
        src = skipPre(src);
        ok = skipPre(ok);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

    it("should re-index", function () {
        let src = `院外処方
                -Ｒｐ）
                -３）アムロジピン錠２．５ｍｇ　１錠
                -　　分３　毎食後　３０日分
                -１）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付
                -@0410対応
                -@有効期限：2020-07-10`;
        let ok = `院外処方
                -Ｒｐ）
                -１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
                -　　分３　毎食後　　　　　　　　　　　　　３０日分
                -２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
                -　　１日２回患部に貼付
                -@0410対応
                -@有効期限：2020-07-10`;
        src = skipPre(src);
        ok = skipPre(ok);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

    it("should support multi-drug items", function(){
        let src = `院外処方
                -Ｒｐ）
                -１）アムロジピン（５）　１錠
                -ロサルタンカリウム（５０）　１錠
                -分１　朝食後　６０日分
                -@0410対応`;
        let dst = `院外処方
                -Ｒｐ）
                -１）アムロジピン（５）　　　　　　　　　　１錠
                -　　ロサルタンカリウム（５０）　　　　　　１錠
                -　　分１　朝食後　　　　　　　　　　　　　６０日分
                -@0410対応`;
        src = skipPre(src);
        dst = skipPre(dst);
        chai.expect(fmt.formatPresc(src)).to.equal(dst);
    })

    it("should handle tonpuku drug", function(){
        let src = `院外処方
        -Ｒｐ）
        -１）ロキソニン錠６０ｍｇ　１錠
        -頭痛時、１回１錠、屯用　１０回分`;
        let dst = `院外処方
        -Ｒｐ）
        -１）ロキソニン錠６０ｍｇ　　　　　　　　　１錠
        -　　頭痛時、１回１錠、屯用　　　　　　　　１０回分`;
        src = skipPre(src);
        dst = skipPre(dst);
        chai.expect(fmt.formatPresc(src)).to.equal(dst);
    });

    it("should handle long line", function(){

        let src = `５）アレンドロン酸錠３５ｍｇ　１錠
                -週１回　起床時　服用後３０分は横にならず、食事もしないこと　８日分（実日数）`;
        let dst = `-１）アレンドロン酸錠３５ｍｇ　　　　　　　１錠
                   -　　週１回　起床時　服用後３０分は横にならず、食事もしないこと
                   -　　　　　　　　　　　　　　　　　　　　　８日分（実日数）`;
        src = skipPre(src);
        dst = skipPre(dst);
        console.log(dst);
        console.log(fmt.formatPresc(src));
        chai.expect(fmt.formatPresc(src)).to.equal(dst);
    });

});

function skipPre(s){
    return s.replace(/^\s*-/mg, "");
}

`５）アレンドロン酸錠３５ｍｇ　　　　　　　１錠
　　分１　起床時　服用後３０分は横にならず、食事もしないこと
　　８日分（１週１回）`
