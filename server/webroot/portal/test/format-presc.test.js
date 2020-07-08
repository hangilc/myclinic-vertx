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
            "　　分３　毎食後　３０日分";
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });
    it("should handle naifuku and gaiyou", function () {
        let src = "１）アムロジピン錠２．５ｍｇ　１錠\n" +
            "　　分３　毎食後　３０日分\n" +
            "２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚\n" +
            "　　１日２回患部に貼付";
        let ok = "１）アムロジピン錠２．５ｍｇ　　　　　　　１錠\n" +
            "　　分３　毎食後　３０日分\n" +
            "２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚\n" +
            "　　１日２回患部に貼付";
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });
    it("should handle prefix", function () {
        let src = `
院外処方
Ｒｐ）
１）アムロジピン錠２．５ｍｇ　１錠
　　分３　毎食後　３０日分
２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付`.substring(1);
        let ok = `
院外処方
Ｒｐ）
１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
　　分３　毎食後　３０日分
２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付`.substring(1);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

    it("should handle control", function () {
        let src = `
院外処方
Ｒｐ）
１）アムロジピン錠２．５ｍｇ　１錠
　　分３　毎食後　３０日分
２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付
@0410対応
@有効期限：2020-07-10
`.substring(1);
        let ok = `
院外処方
Ｒｐ）
１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
　　分３　毎食後　３０日分
２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付
@0410対応
@有効期限：2020-07-10
`.substring(1);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

    it("should re-index", function () {
        let src = `
院外処方
Ｒｐ）
３）アムロジピン錠２．５ｍｇ　１錠
　　分３　毎食後　３０日分
１）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付
@0410対応
@有効期限：2020-07-10
`.substring(1);
        let ok = `
院外処方
Ｒｐ）
１）アムロジピン錠２．５ｍｇ　　　　　　　１錠
　　分３　毎食後　３０日分
２）インサイドパップ１０ｃｍｘ１４ｃｍ　１０枚
　　１日２回患部に貼付
@0410対応
@有効期限：2020-07-10
`.substring(1);
        chai.expect(fmt.formatPresc(src)).to.equal(ok);
    });

});