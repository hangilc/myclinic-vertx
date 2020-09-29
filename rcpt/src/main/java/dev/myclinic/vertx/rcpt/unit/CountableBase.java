package dev.myclinic.vertx.rcpt.unit;

class CountableBase implements Countable {

    //private static Logger logger = LoggerFactory.getLogger(CountableBase.class);
    private int count = 1;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void incCount(int n) {
        count += n;
    }

    public void setCount(int c){
        this.count = c;
    }

}
