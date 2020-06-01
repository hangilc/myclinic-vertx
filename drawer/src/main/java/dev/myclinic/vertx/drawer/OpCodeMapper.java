package dev.myclinic.vertx.drawer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hangil on 2017/05/17.
 */
public class OpCodeMapper {

    private Map<String, OpCode> map = new HashMap<>();

    public OpCodeMapper(){
        for(OpCode opCode: OpCode.values()){
            map.put(opCode.getIdent(), opCode);
        }
    }

    public OpCode map(String ident){
        return map.get(ident);
    }

}
