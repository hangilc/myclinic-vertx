import {TextDisp} from "../practice/text/text-disp.js";
import {TextEdit} from "../practice/text/text-edit.js";
import {parseElement} from "../js/parse-element.js";

let textEditTemplate = `
    <div class="mt-2">
        <textarea class="form-control x-textarea" rows="6"></textarea>
        <div class="form-inline mt-2">
            <a href="javascript:void(0)" class="x-enter">入力</a>
            <a href="javascript:void(0)" class="x-cancel ml-2">キャンセル</a>
            <a href="javascript:void(0)" class="x-copy-memo ml-2">引継ぎコピー</a>
            <a href="javascript:void(0)" class="x-delete ml-2">削除</a>
            <div class="dropbox x-shohousen-menu d-none">
                <button type="button" class="btn btn-link dropdown-toggle"
                        data-toggle="dropdown">処方箋</button>
                <div class="dropdown-menu">
                    <a href="javascript:void(0)" class="x-shohousen dropdown-item">処方箋発行</a>
                    <a href="javascript:void(0)" class="x-shohousen-fax dropdown-item">処方箋FAX</a>
                    <a href="javascript:void(0)" class="x-format-presc dropdown-item">処方箋整形</a>
                    <a href="javascript:void(0)" class="x-preview-current dropdown-item">編集中表示</a>
                </div>
            </div>
            <a href="javascript:void(0)" class="x-copy ml-2">コピー</a>
        </div>
    </div>
`;

describe("presc", function(){

    it("should display presc", function(){
        let content = "院外処方\nＲｐ）\nabc　　　def";
        let text = {content};
        let textDisp = new TextDisp($("<div></div>"));
        textDisp.init();
        textDisp.set(text);
        let modified = "院外処方<br>\nＲｐ）<br>\nabc   def";
        chai.expect(textDisp.ele.html()).to.equal(modified);
    });

    it("should display normal text", function(){
        let content = "Ｒｐ）\nabc　　　def";
        let text = {content};
        let textDisp = new TextDisp($("<div></div>"));
        textDisp.init();
        textDisp.set(text);
        let modified = "Ｒｐ）<br>\nabc　　　def";
        chai.expect(textDisp.ele.html()).to.equal(modified);
    });

    it("should set textarea appropriately for presc", function(){
        let ele = $(textEditTemplate);
        let textEdit = new TextEdit(ele, parseElement(ele), null);
        let content = "院外処方\nＲｐ）\nabc　　　def";
        let text = {content};
        textEdit.init(text);
        let val = textEdit.textareaElement.val();
        let expected = "院外処方\nＲｐ）\nabc   def";
        chai.expect(val).to.equal(expected);
    });

    it("should set textarea appropriately for regular text", function(){
        let ele = $(textEditTemplate);
        let textEdit = new TextEdit(ele, parseElement(ele), null);
        let content = "Ｒｐ）\nabc　　　def";
        let text = {content};
        textEdit.init(text);
        let val = textEdit.textareaElement.val();
        let expected = "Ｒｐ）\nabc　　　def";
        chai.expect(val).to.equal(expected);
    });

    it("should save for presc", async function(){
        let ele = $(textEditTemplate);
        let enteredContent;
        let rest = {
            updateText: async text => {
                enteredContent = text.content;
                return true;
            },
            getText: async textId => {
                return null;
        }
        }
        let textEdit = new TextEdit(ele, parseElement(ele), rest);
        let text = {content: "", textId: 2, visitId: 1};
        textEdit.init(text);
        let content = "院外処方\nＲｐ）\nabc   def";
        textEdit.textareaElement.val(content);
        let val = textEdit.textareaElement.val();
        await textEdit.doEnter();
        let expected = "院外処方\nＲｐ）\nabc　　　def";
        chai.expect(enteredContent).to.equal(expected);
    });

    it("should save for regular content", async function(){
        let ele = $(textEditTemplate);
        let enteredContent;
        let rest = {
            updateText: async text => {
                enteredContent = text.content;
                return true;
            },
            getText: async textId => {
                return null;
        }
        }
        let textEdit = new TextEdit(ele, parseElement(ele), rest);
        let text = {content: "", textId: 2, visitId: 1};
        textEdit.init(text);
        let content = "Ｒｐ）\nabc   def　　　ghi";
        textEdit.textareaElement.val(content);
        let val = textEdit.textareaElement.val();
        await textEdit.doEnter();
        chai.expect(enteredContent).to.equal(content);
    });



});
