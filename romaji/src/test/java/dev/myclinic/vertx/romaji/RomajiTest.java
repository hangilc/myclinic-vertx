package dev.myclinic.vertx.romaji;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RomajiTest {

    @Test
    public void testRegular(){
        String src = "あ";
        String result = Romaji.toRomaji(src);
        assertEquals("a", result);
    }

    @Test
    public void testTsuDakuon() {
        String src = "づ";
        String result = Romaji.toRomaji(src);
        assertEquals("zu", result);
    }

    @Test
    public void testIssei() {
        String src = "いっせい";
        String result = Romaji.toRomaji(src);
        assertEquals("issei", result);
    }

    @Test
    public void testBar(){
        String src = "ー";
        String result = Romaji.toRomaji(src);
        assertEquals("", result);
    }

}
