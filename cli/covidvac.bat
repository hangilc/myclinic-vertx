@echo off
REM mvn -pl cli dependency:copy-dependencies
java -cp cli\target\dependency\*;cli\target\cli-1.0.0-SNAPSHOT.jar dev.myclinic.vertx.cli.covidvaccine.CovidVaccine %*