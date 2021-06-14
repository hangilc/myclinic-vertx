package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.dto.PatientDTO;

import java.time.LocalDate;

class CovidMisc {

    public static CovidVaccine.RegularPatient patientToRegularPatient(PatientDTO patient){
        int age = Misc.ageAt(LocalDate.parse(patient.birthday), LocalDate.of(2022, 3, 31));
        return new CovidVaccine.RegularPatient(patient.patientId,
                patient.lastName + patient.firstName,
                age, patient.phone, "*");
    }

}
