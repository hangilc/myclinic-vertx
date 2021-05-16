package dev.myclinic.vertx.camelcomp.rcpt;

import dev.myclinic.vertx.master.Downloader;

import java.io.IOException;
import java.nio.file.Path;

public class RcptMasterDownloader {

    public static void downloadShinryou(Path dstDir) throws IOException {
        Path dst = dstDir.resolve(Downloader.DEFAULT_SHINRYOU_FILENAME);
        Downloader.downloadShinryou(dst);
    }

    public static void downloadIyakuhin(Path dstDir) throws IOException {
        Path dst = dstDir.resolve(Downloader.DEFAULT_IYAKUHIN_FILENAME);
        Downloader.downloadIyakuhin(dst);
    }

    public static void downloadKizai(Path dstDir) throws IOException {
        Path dst = dstDir.resolve(Downloader.DEFAULT_KIZAI_FILENAME);
        Downloader.downloadKizai(dst);
    }

    public static void downloadByoumei(Path dstDir) throws IOException {
        Path dst = dstDir.resolve(Downloader.DEFAULT_SHOUBYOUMEI_FILENAME);
        Downloader.downloadShoubyoumei(dst);
    }

    public static void downloadShuushokugo(Path dstDir) throws IOException {
        Path dst = dstDir.resolve(Downloader.DEFAULT_SHUUSHOKUGO_FILENAME);
        Downloader.downloadShuushokugo(dst);
    }

}
