package dev.myclinic.vertx.rcpt.check;

class CheckShoshinSaishin extends CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckShoshinSaishin.class);

    CheckShoshinSaishin(Scope scope) {
        super(scope);
    }

    void check(){
        forEachVisit(visit -> {
            int nShoshin = countShoshinGroup(visit);
            int nSaishin = countSaishinGroup(visit);
            int n = nShoshin + nSaishin;
            if( n == 0 ){
                error("初診再診もれ");
            } else if( n > 1 ){
                error("初診再診重複");
            }
        });
    }

}
