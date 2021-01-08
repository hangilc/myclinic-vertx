// export function shohousenTextContentDispToData(content){
//     if (content.startsWith("院外処方")) {
//         return content.replace(/ /ug, "　"); // replace ascii space to zenkaku space
//     } else {
//         return content;
//     }
// }
//
// export function shohousenTextContentDataToDisp(content){
//     if (content.startsWith("院外処方")) {
//         return content.replace(/\u{3000}/ug, " "); // replace zenkaku space to ascii space
//     } else {
//         return content;
//     }
// }

export async function createShohousenOps(text, reqOpts, rest) {
    let visit = await rest.getVisit(text.visitId);
    let visitDate = visit.visitedAt.substring(0, 10);
    let req = {};
    req.clinicInfo = await rest.getClinicInfo();
    req.hoken = await rest.getHoken(text.visitId);
    req.patient = await rest.getPatient(visit.patientId);
    let rcptAge = await rest.calcRcptAge(req.patient.birthday, visitDate);
    req.futanWari = await rest.calcFutanWari(req.hoken, rcptAge, visit);
    req.issueDate = visitDate;
    req.drugs = text.content;
    Object.assign(req, reqOpts);
    return await rest.shohousenDrawer(req);
}


export async function createShohousenPdfForFax(text, rest){
    let visit = await rest.getVisit(text.visitId);
    let patient = await rest.getPatient(visit.patientId);
    let name = await rest.convertToRomaji(patient.lastNameYomi + patient.firstNameYomi);
    let savePath = await rest.getShohousenSavePdfPath(name, text.textId,
        patient.patientId, visit.visitedAt.substring(0, 10));
    let ops = await createShohousenOps(text, {color: "black"}, rest);
    let tmpPath = await rest.createTempFileName("shohousen", ".pdf");
    await rest.saveDrawerAsPdf([ops], "A5", tmpPath);
    await rest.putStampOnPdf(tmpPath, "shohousen-gray", savePath);
    await rest.deleteFile(tmpPath);
    return savePath;
}


