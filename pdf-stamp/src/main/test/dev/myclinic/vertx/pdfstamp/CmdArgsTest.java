package dev.myclinic.vertx.pdfstamp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CmdArgsTest {

    @Test
    public void testCmdArgs(){
        String[] args = new String[]{
                "base.pdf",
                "stamp.pdf"
        };
        CmdArgs cargs = CmdArgs.parse(args);
        assertEquals("base.pdf", cargs.lowerPdf);
        assertEquals("stamp.pdf", cargs.upperPdf);
    }

}
