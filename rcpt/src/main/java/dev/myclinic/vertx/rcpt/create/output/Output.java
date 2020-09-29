package dev.myclinic.vertx.rcpt.create.output;

import dev.myclinic.vertx.util.NumberUtil;

import java.io.PrintStream;

public class Output {

    //private static Logger logger = LoggerFactory.getLogger(Output.class);
    private PrintStream outStream;

    public Output(PrintStream outStream) {
        this.outStream = outStream;
    }

    public void print(String s){
        outStream.printf("%s\n", s);
    }

    public void printInt(String key, int value){
        outStream.printf("%s %d\n", key, value);
    }

    public void printStr(String key, String value){
        outStream.printf("%s %s\n", key, value);
    }

    public void printTekiyou(String shuukei, String body, int tanka, int count){
        outStream.printf("tekiyou %s:%s:%d:%d\n", shuukei, body, tanka, count);
    }

    public void printTekiyouAux(String text){
        if( text != null && !text.isEmpty() ){
            outStream.printf("tekiyou_aux {left-margin:8.0} %s\n", text);
        }
    }

    public void beginDrug(String shuukei, int tanka, int count){
        outStream.printf("tekiyou_begin_drugs %s:%d:%d\n", shuukei, tanka, count);
   }

    public void addDrug(String name, double amount, String unit){
        outStream.printf("tekiyou_drug %s:%s%s\n", name, NumberUtil.formatNumber(amount), unit);
    }

    public void endDrug(){
        outStream.println("tekiyou_end_drugs");
    }

    public void printShuukei(String prefix, Integer tanka, Integer count, Integer ten){
        if( ten != null && ten == 0 ){
            return;
        }
        if( tanka != null ){
            outStream.printf("%s.tanka %d\n", prefix, tanka);
        }
        if( count != null ){
            outStream.printf("%s.kai %d\n", prefix, count);
        }
        if( ten != null ){
            outStream.printf("%s.ten %d\n", prefix, ten);
        }
    }

}
