package dev.myclinic.vertx.romaji;

import java.util.HashMap;
import java.util.Map;

public class Romaji {

    private StringBuilder sb = new StringBuilder();
    private enum Mode { Regular, Kitsuon };
    private Mode mode = Mode.Regular;

    private void append(String romaji){
        if( mode == Mode.Regular ){
            sb.append(romaji);
        } else if( mode == Mode.Kitsuon ) {
            char ch = romaji.charAt(0);
            if( ch == 'a' || ch == 'i' || ch == 'u' || ch == 'e' || ch == 'o' ){
                sb.append(romaji);
            } else {
                sb.append(ch);
                sb.append(romaji);
            }
            mode = Mode.Regular;
        }
    }

    private String convert(String hiragana){
        for (int i = 0; i < hiragana.length(); i++) {
            char c = hiragana.charAt(i);
            if( c == 'っ' ){
                mode = Mode.Kitsuon;
                continue;
            }
            if (i < hiragana.length() - 1) {
                char cc = hiragana.charAt(i + 1);
                if (cc == 'ゃ') {
                    if (yaMap.containsKey(c)) {
                        append(yaMap.get(c));
                        i += 1;
                        continue;
                    }
                } else if (cc == 'ゅ') {
                    if (yuMap.containsKey(c)) {
                        append(yuMap.get(c));
                        i += 1;
                        continue;
                    }
                } else if (cc == 'ょ') {
                    if (yoMap.containsKey(c)) {
                        append(yoMap.get(c));
                        i += 1;
                        continue;
                    }
                }
            }
            if( regularMap.containsKey(c) ) {
                append(regularMap.get(c));
            } else {
                System.err.println("Cannot convert to romaji: " + c);
            }
        }
        return sb.toString();
    }

    public static String toRomaji(String hiragana) {
        return new Romaji().convert(hiragana);
    }

    private final static Map<Character, String> yaMap = new HashMap<>();
    private final static Map<Character, String> yuMap = new HashMap<>();
    private final static Map<Character, String> yoMap = new HashMap<>();

    static {
        yaMap.put('き', "kya");
        yaMap.put('し', "sha");
        yaMap.put('ち', "cha");
        yaMap.put('に', "nya");
        yaMap.put('ひ', "hya");
        yaMap.put('み', "mya");
        yaMap.put('り', "rya");
        yaMap.put('ぎ', "gya");
        yaMap.put('じ', "ja");
        yaMap.put('び', "bya");
    }

    static {
        yuMap.put('き', "kyu");
        yuMap.put('し', "shu");
        yuMap.put('ち', "chu");
        yuMap.put('に', "nyu");
        yuMap.put('ひ', "hyu");
        yuMap.put('み', "myu");
        yuMap.put('り', "ryu");
        yuMap.put('ぎ', "gyu");
        yuMap.put('じ', "ju");
        yuMap.put('び', "byu");
    }

    static {
        yoMap.put('き', "kyo");
        yoMap.put('し', "sho");
        yoMap.put('ち', "cho");
        yoMap.put('に', "nyo");
        yoMap.put('ひ', "hyo");
        yoMap.put('み', "myo");
        yoMap.put('り', "ryo");
        yoMap.put('ぎ', "gyo");
        yoMap.put('じ', "jo");
        yoMap.put('び', "byo");
    }

    private final static Map<Character, String> regularMap = new HashMap<>();

    static {
        regularMap.put('あ', "a");
        regularMap.put('い', "i");
        regularMap.put('う', "u");
        regularMap.put('え', "e");
        regularMap.put('お', "o");
        regularMap.put('か', "ka");
        regularMap.put('き', "ki");
        regularMap.put('く', "ku");
        regularMap.put('け', "ke");
        regularMap.put('こ', "ko");
        regularMap.put('さ', "sa");
        regularMap.put('し', "shi");
        regularMap.put('す', "su");
        regularMap.put('せ', "se");
        regularMap.put('そ', "so");
        regularMap.put('た', "ta");
        regularMap.put('ち', "chi");
        regularMap.put('つ', "tsu");
        regularMap.put('て', "te");
        regularMap.put('と', "to");
        regularMap.put('な', "na");
        regularMap.put('に', "ni");
        regularMap.put('ぬ', "nu");
        regularMap.put('ね', "ne");
        regularMap.put('の', "no");
        regularMap.put('は', "ha");
        regularMap.put('ひ', "hi");
        regularMap.put('ふ', "fu");
        regularMap.put('へ', "he");
        regularMap.put('ほ', "ho");
        regularMap.put('ま', "ma");
        regularMap.put('み', "mi");
        regularMap.put('む', "mu");
        regularMap.put('め', "me");
        regularMap.put('も', "mo");
        regularMap.put('や', "ya");
        regularMap.put('ゆ', "yu");
        regularMap.put('よ', "yo");
        regularMap.put('ら', "ra");
        regularMap.put('り', "ri");
        regularMap.put('る', "ru");
        regularMap.put('れ', "re");
        regularMap.put('ろ', "ro");
        regularMap.put('わ', "wa");
        regularMap.put('を', "wo");
        regularMap.put('ん', "n");
        regularMap.put('が', "ga");
        regularMap.put('ぎ', "gi");
        regularMap.put('ぐ', "gu");
        regularMap.put('げ', "ge");
        regularMap.put('ご', "go");
        regularMap.put('ざ', "za");
        regularMap.put('じ', "ji");
        regularMap.put('ず', "zu");
        regularMap.put('ぜ', "ze");
        regularMap.put('ぞ', "zo");
        regularMap.put('だ', "da");
        regularMap.put('ぢ', "di");
        regularMap.put('づ', "zu");
        regularMap.put('で', "de");
        regularMap.put('ど', "do");
        regularMap.put('ば', "ba");
        regularMap.put('び', "bi");
        regularMap.put('ぶ', "bu");
        regularMap.put('べ', "be");
        regularMap.put('ぼ', "bo");
        regularMap.put('ぱ', "pa");
        regularMap.put('ぴ', "pi");
        regularMap.put('ぷ', "pu");
        regularMap.put('ぺ', "pe");
        regularMap.put('ぽ', "po");
    }

}
