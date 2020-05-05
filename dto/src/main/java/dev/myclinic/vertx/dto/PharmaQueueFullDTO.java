package dev.myclinic.vertx.dto;

/**
 * Created by hangil on 2017/06/11.
 */
public class PharmaQueueFullDTO {
    public int visitId;
    public PatientDTO patient;
    public dev.myclinic.vertx.dto.PharmaQueueDTO pharmaQueue;
    public dev.myclinic.vertx.dto.WqueueDTO wqueue;
}
