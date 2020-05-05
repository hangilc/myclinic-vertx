package dev.myclinic.vertx.dto;

public class ResolvedStockDrugDTO {

    public int queryIyakuhincode;
    public int resolvedIyakuhincode;

    public static dev.myclinic.vertx.dto.ResolvedStockDrugDTO create(int query, int resolved){
        dev.myclinic.vertx.dto.ResolvedStockDrugDTO result = new dev.myclinic.vertx.dto.ResolvedStockDrugDTO();
        result.queryIyakuhincode = query;
        result.resolvedIyakuhincode = resolved;
        return result;
    }

    @Override
    public String toString() {
        return "ResolvedStockDrugDTO{" +
                "queryIyakuhincode=" + queryIyakuhincode +
                ", resolvedIyakuhincode=" + resolvedIyakuhincode +
                '}';
    }
}
