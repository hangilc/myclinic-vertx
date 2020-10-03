let html = `

<div class="row pane" id="cashier-top">
    <h3 class="col-sm-2">会計</h3>
    <div class="col-sm-10">
        <button class="btn btn-secondary" id="cashier-refresh-button">更新</button>
    </div>
</div>

<div>
<table class="table" id="cashier-wqueue-table">
    <thead>
        <tr>
            <th>状態</th>
            <th>#</th>
            <th>氏名</th>
            <th>よみ</th>
            <th>性別</th>
            <th>生年月日</th>
            <th>年齢</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody></tbody>
</table>
</div>

<div class="modal fade" id="cashier-charge-dialog" data-backdrop="static"
     tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Title</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <pre class="cashier-part-charge-detail"></pre>
                <div class="cashier-part-charge-summary"></div>
                <div class="cashier-part-charge-value"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">キャンセル</button>
                <button type="button" class="btn btn-primary cashier-part-enter">会計終了</button>
            </div>
        </div>
    </div>
</div>
`;

export function getHtml(){
    return html;
}

export async function initCashier(pane){

    let {wqueueStateCodeToRep, WqueueStateWaitCashier, sexToRep} = await import("../js/consts.js");

    pane.addEventListener("onreloaded", async event => await update());

    class ChargeDialog {
        constructor(ele) {
            this.ele = ele;
            this.onEnterCallback = () => {};
            this.ele.find(".cashier-part-enter").on("click", event => {
                this.onEnterCallback();
            });
            this.ele.on("hide.bs.modal", event => {
                this.onEnterCallback = () => {};
            })
        }

        set title(value){
            this.ele.find(".modal-title").text(value);
        }

        set detail(value){
            this.ele.find(".cashier-part-charge-detail").html(value);
        }

        set summary(value){
            this.ele.find(".cashier-part-charge-summary").text(value);
        }

        set value(amount){
            this.ele.find(".cashier-part-charge-value").text(amount);
        }

        show(){
            this.ele.modal();
        }

        hide(){
            this.ele.modal('hide');
        }

        setOnEnter(f){
            this.onEnterCallback = async () => {
                this.onEnterCallback = () => {};
                await f();
            };
        }
    }

    let chargeDialog = new ChargeDialog($("#cashier-charge-dialog"));

    class WqueueTable {
        constructor(ele, chargeDialog){
            this.ele = ele;
            this.chargeDialog = chargeDialog;
        }

        clear(){
            this.ele.find("tbody").html("");
        }

        addRow(obj){
            let tbody = this.ele.find("tbody");
            let tr = $("<tr>");
            let td;
            td = $("<td>").text(obj.state);
            tr.append(td);
            td = $("<td>").text(obj.patientId);
            tr.append(td);
            td = $("<td>").text(obj.name);
            tr.append(td);
            td = $("<td>").text(obj.nameYomi);
            tr.append(td);
            td = $("<td>").text(obj.sex);
            tr.append(td);
            td = $("<td>").text(obj.birthday);
            tr.append(td);
            td = $("<td>").text(obj.age);
            tr.append(td);
            td = $("<td>").append(obj.commands);
            tr.append(td);
            tbody.append(tr);
        }

        addRowByWqueue(wq){
            let name = wq.patient.lastName + wq.patient.firstName;
            let yomi = wq.patient.lastNameYomi + wq.patient.firstNameYomi
            let birthday = moment(wq.patient.birthday).format("YYYY年MM月DD日");
            let age = moment().diff(wq.patient.birthday, "years");
            let commands = null;
            if( wq.wqueue.waitState === WqueueStateWaitCashier ){
                let cashierButton = $("<button>", {class: "btn btn-primary"}).text("会計");
                cashierButton.on("click", async event => {
                    let meisai = await rest.getMeisai(wq.visit.visitId);
                    let charge = await rest.getCharge(wq.visit.visitId);
                    let dialog = chargeDialog;
                    dialog.title = `会計：（${wq.patient.patientId}）${name}（${yomi}）`;
                    dialog.detail = meisai.sections.map(sect => {
                        return `${sect.label}：${sect.sectionTotalTen.toLocaleString()} 点`;
                    }).join("\n");
                    dialog.summary = `総点：${meisai.totalTen.toLocaleString()} 点、負担割：${meisai.futanWari}割`;
                    //dialog.value = `請求額：${meisai.charge.toLocaleString()} 円`;
                    dialog.value = `請求額：${charge.charge.toLocaleString()} 円`;
                    dialog.setOnEnter(async () => {
                        await rest.finishCharge(wq.visit.visitId, charge.charge, moment());
                        dialog.hide();
                        update();
                    });
                    dialog.show();
                });
                commands = cashierButton;
            }
            let obj = {
                state: wqueueStateCodeToRep(wq.wqueue.waitState),
                patientId: wq.patient.patientId,
                name: name,
                nameYomi: yomi,
                sex: sexToRep(wq.patient.sex),
                birthday: birthday,
                age: age + "才",
                commands: commands
            }
            this.addRow(obj);
        }
    }

    let wqTable = new WqueueTable($("#cashier-wqueue-table"), $("#cashier-charge-dialog"));

    async function update(){
        console.log("cashier-update");
        let wqList = await rest.listWqueueFull();
        wqTable.clear();
        for(let wq of wqList){
            wqTable.addRowByWqueue(wq);
        }
    }

    $("#cashier-refresh-button").on("click", event => update());

    $("#cashier-top").on("pane_shown", event => update());

    await update();
}