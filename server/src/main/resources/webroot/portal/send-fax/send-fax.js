let html = `
<h3>ファックス送信</h3>

<form class="form" id="send-fax-form">
    <label for="send-fax-fax-number-input">送信先電話番号</label>
    <input type="text" id="send-fax-fax-number-input" class="form-control" value="+81"/>
    <label for="send-fax-pdf-file-input" class="mt-3">PDF ファイル</label>
    <a href="javascript:void(0)" class="ml-2" id="send-fax-preview-lilnk">プレビュー</a>
    <input type="input" id="send-fax-pdf-file-input" class="form-control"/>
    <div class="mt-3">
        <button class="btn btn-primary">送信</button>
    </div>
</form>

<div id="send-fax-progress-wrapper" class="mt-3"></div>

<template id="send-fax-progress-template">
    <div class="border founded mb-3 p-2">
        <div class="x-fax-number"></div>
        <div class="x-pdf-file mt-2"></div>
        <div class="x-message mt-4"></div>
        <div class="mt-4">
            <button class="btn btn-secondary x-send">再送信</button>
            <a href="javascript:void(0)" class="x-close ml-2">閉じる</a>
        </div>
    </div>
</template>
`;

export function getHtml(){
    return html;
}

export async function initSendFax(pane) {
    let {Component} = await import("../js/component.js");
    let {parseElement} = await import("../js/parse-element.js");

    class Progress extends Component {
        constructor(ele, map, rest) {
            super(ele, map, rest);
            this.faxNumberElement = map.faxNumber;
            this.pdfFileElement = map.pdfFile;
            this.messageElement = map.message;
            this.sendElement = map.send;
            this.closeElement = map.close;
        }

        addMessage(msg) {
            let e = $("<div>").text(msg);
            this.messageElement.append(e);
        }

        async poll() {
            let status = await this.rest.pollFax(this.faxSid);
            this.addMessage(status);
            if (status === "sending" || status === "processing" || status === "queued") {
                setTimeout(() => this.poll(), 10000);
            }
        }

        init(faxNumber, pdfFile, faxSid) {
            this.faxNumberElement.text(faxNumber);
            this.pdfFileElement.text(pdfFile);
            this.faxSid = faxSid;
            this.closeElement.on("click", event => this.remove());
        }

        start() {
            this.addMessage("started");
            setTimeout(() => {
                this.poll();
            }, 10000);
        }
    }

    class ProgressFactory {
        constructor() {
            this.html = $("template#send-fax-progress-template").html();
        }

        create(faxNumber, pdfFile, faxSid) {
            let ele = $(this.html);
            let map = parseElement(ele);
            let compProgress = new Progress(ele, map, rest);
            compProgress.init(faxNumber, pdfFile, faxSid);
            return compProgress;
        }
    }

    let progressFactory = new ProgressFactory();

    $("#send-fax-form").on("submit", async event => {
        event.preventDefault();
        let faxNumber = $("#send-fax-fax-number-input").val();
        let pdfFile = $("#send-fax-pdf-file-input").val();
        let faxSid = await rest.sendFax(faxNumber, pdfFile);
        let progress = progressFactory.create(faxNumber, pdfFile, faxSid);
        progress.appendTo($("#send-fax-progress-wrapper"));
        progress.start();
    });

    $("#send-fax-preview-lilnk").on("click", event => {
        let file = $("#send-fax-pdf-file-input").val();
        let url = rest.url("/show-pdf", {file});
        window.open(url, "_blank");
    });

}
