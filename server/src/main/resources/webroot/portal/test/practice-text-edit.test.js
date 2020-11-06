import * as te from "../practice/text-edit.js";

describe("text-edit", function(){

    it("should detect memo", function(){
        let content = "●検査結果は電話で連絡する。";
        chai.expect(te.hasMemo(content)).to.equal(true);
    });

    it("should detect memo starting with star", function(){
        let content = "★レボフロキサシン禁忌★";
        chai.expect(te.hasMemo(content)).to.equal(true);
    });

    it("should not detect false memo", function(){
        let content = "体調かわりない。\n●検査結果は電話で連絡する。";
        chai.expect(te.hasMemo(content)).to.equal(false);
    });

    it("should not detect false memo (star)", function(){
        let content = "体調かわりない。\n★レボフロキサシン禁忌★";
        chai.expect(te.hasMemo(content)).to.equal(false);
    });

});