package dev.myclinic.vertx.server;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.twilio.Twilio;
import com.twilio.rest.fax.v1.Fax;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.io.File;
import java.net.URI;
import java.util.function.Consumer;

class SendFax {

    private static final String bucketName;
    private static final String twilioPhone;
    private static final String cloudFrontUrl;
    private static final String cloudFrontUser;
    private static final String cloudFrontPass;

    static {
        String twilioSid = System.getenv("TWILIO_SID");
        String twilioToken = System.getenv("TWILIO_TOKEN");
        Twilio.init(twilioSid, twilioToken);
        bucketName = System.getenv("MYCLINIC_S3_FAX_BUCKET");
        twilioPhone = System.getenv("TWILIO_PHONE");
        cloudFrontUrl = System.getenv("MYCLINIC_CLOUDFRONT_FAX_URL");
        cloudFrontUser = System.getenv("MYCLINIC_CLOUDFRONT_FAX_USER");
        cloudFrontPass = System.getenv("MYCLINIC_CLOUDFRONT_FAX_PASS");
    }

    public static void send(String faxNumber, String pdfFile, Consumer<String> progressCallback,
                     Consumer<String> startedCallback, Vertx vertx) {
        File srcFile = new File(pdfFile);
        try {
            String key = srcFile.getName();
            uploadToS3(pdfFile, bucketName, key);
            Fax fax = sendFax(faxNumber, key, twilioPhone);
            String faxSid = fax.getSid();
            startedCallback.accept(faxSid);
            int pollInterval = 10000; // 10 seconds
            vertx.setTimer(pollInterval, createTimerCallback(faxSid, progressCallback,
                    pollInterval, vertx, 20));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String pollStatus(String faxSid){
        return getFaxStatus(faxSid).toString();
    }

    private static String createCloudFrontUrl(String key) {
        return String.format("https://%s:%s@%s/%s",
                cloudFrontUser, cloudFrontPass, cloudFrontUrl, key);
    }

    private static Fax sendFax(String faxNumber, String key, String fromPhone) {
        return Fax.creator(faxNumber, URI.create(createCloudFrontUrl(key))).setFrom(fromPhone).create();
    }

    private static Fax.Status getFaxStatus(String faxSid) {
        Fax fax = Fax.fetcher(faxSid).fetch();
        return fax.getStatus();
    }

    private static Fax.Status cancelFax(String faxSid) {
        Fax fax = Fax.updater(faxSid).setStatus(Fax.UpdateStatus.CANCELED).update();
        return fax.getStatus();
    }

    private static Handler<Long> createTimerCallback(String faxSid, Consumer<String> progressCallback,
                                              int pollInterval, Vertx vertx, int allowedRetries) {
        return id -> {
            Fax.Status status = getFaxStatus(faxSid);
            progressCallback.accept(status.toString());
            if (status == Fax.Status.SENDING || status == Fax.Status.PROCESSING ||
                    status == Fax.Status.QUEUED) {
                if (allowedRetries > 0) {
                    vertx.setTimer(pollInterval, createTimerCallback(faxSid, progressCallback,
                            pollInterval, vertx, allowedRetries - 1));
                } else {
                    progressCallback.accept("timeout");
                }
            }
        };
    }

    private static void uploadToS3(String srcFile, String dstBucket, String dstKey) {
        File file = new File(srcFile);
        if( !file.exists() ){
            throw new RuntimeException("No such file: " + srcFile);
        }
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
        s3.putObject(dstBucket, dstKey, file);
    }

}
