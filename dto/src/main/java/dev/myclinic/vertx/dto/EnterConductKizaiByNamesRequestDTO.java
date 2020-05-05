package dev.myclinic.vertx.dto;

public class EnterConductKizaiByNamesRequestDTO {

    public String name;
    public double amount;

    public static dev.myclinic.vertx.dto.EnterConductKizaiByNamesRequestDTO create(String name, double amount){
        dev.myclinic.vertx.dto.EnterConductKizaiByNamesRequestDTO result = new dev.myclinic.vertx.dto.EnterConductKizaiByNamesRequestDTO();
        result.name = name;
        result.amount = amount;
        return result;
    }

    @Override
    public String toString() {
        return "EnterConductKizaiByNamesRequestDTO{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
