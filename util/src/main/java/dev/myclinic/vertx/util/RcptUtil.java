package dev.myclinic.vertx.util;

public class RcptUtil {

	public static int touyakuKingakuToTen(double kingaku){
		if( kingaku <= 15.0 ){
			return 1;
		} else {
			return (int)Math.ceil((kingaku - 15)/10.0) + 1;
		}
	}

	public static int shochiKingakuToTen(double kingaku){
		if( kingaku <= 15 )
			return 0;
		else
			return (int)Math.ceil((kingaku - 15)/10) + 1;
	}

	public static int kizaiKingakuToTen(double kingaku){
		return (int)Math.round(kingaku/10.0);
	}

	public static int calcRcptAge(int bdYear, int bdMonth, int bdDay, int atYear, int atMonth){
	    int age;
		age = atYear - bdYear;
		if( atMonth < bdMonth ){
			age -= 1;
		} else if( atMonth == bdMonth ){
			if( bdDay != 1 ){
				age -= 1;
			}
		}
		return age;
	}

	public static int calcCharge(int ten, int futanWari){
		int c = ten * futanWari;
		int r = c % 10;
		if( r < 5 )
			c -= r;
		else
			c += (10 - r);
		return c;
	}

}