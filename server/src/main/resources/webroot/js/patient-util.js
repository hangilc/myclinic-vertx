
export function patientIdRep(patientId){
    return `${patientId}`.padStart(4, "0");
}