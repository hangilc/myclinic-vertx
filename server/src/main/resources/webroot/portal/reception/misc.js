import {openPrintDialog} from "../js/print-dialog.js";

export async function printReceipt(rest, clinicInfo, meisai, patient, visit, chargeValue = null){
    let req = {
        meisai,
        patient,
        visit,
        charge: chargeValue,
        clinicInfo: clinicInfo
    }
    let ops = await rest.receiptDrawer(req);
    return await openPrintDialog("領収書", null, [ops], "receipt");
}