package dev.myclinic.vertx.master;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloader {

    private static String startPageUrl = "http://www.iryohoken.go.jp/shinryohoshu/downloadMenu/";
    public static final String DEFAULT_SHINRYOU_FILENAME = "s.zip";
    public static final String DEFAULT_IYAKUHIN_FILENAME = "y.zip";
    public static final String DEFAULT_KIZAI_FILENAME = "t.zip";
    public static final String DEFAULT_SHOUBYOUMEI_FILENAME = "b.zip";
    public static final String DEFAULT_SHUUSHOKUGO_FILENAME = "z.zip";

    public static void setStartPageUrl(String url){
        startPageUrl = url;
    }

    public static void downloadShinryou(Path filename) throws IOException {
        download("医科診療行為マスター", filename);
    }

    public static void downloadIyakuhin(Path filename) throws IOException {
        download("医薬品マスター", filename);
    }

    public static void downloadKizai(Path filename) throws IOException {
        download("特定器材マスター", filename);
    }

    public static void downloadShoubyoumei(Path filename) throws IOException {
        download("傷病名マスター", filename);
    }

    public static void downloadShuushokugo(Path filename) throws IOException {
        download("修飾語マスター", filename);
    }

    private static void download(String anchorText, Path filepath) throws IOException {
        try(WebClient webClient = new WebClient()){
            HtmlPage page = webClient.getPage(startPageUrl);
            HtmlAnchor anchor = page.getAnchorByText(anchorText);
            Page downloadPage = anchor.click();
            WebResponse webResponse = downloadPage.getWebResponse();
            try(InputStream in = webResponse.getContentAsStream();
                OutputStream out = Files.newOutputStream(filepath)){
                byte[] buffer = new byte[1024];
                int len = in.read(buffer);
                while( len >= 0 ){
                    out.write(buffer, 0, len);
                    len = in.read(buffer);
                }
            }
        }
    }

}
