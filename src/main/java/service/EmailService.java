package service;

import okhttp3.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private final OkHttpClient httpClient = new OkHttpClient();
    private final String mailgunApiKey = System.getenv("MAILGUN_TOKEN");
    private final String mailgunDomain = "ashutoxh.me";

    public void sendEmail(String to, String token) {
        String verificationLink = "https://ashutoxh.me/v1/user/verify?token=" + token;
        logger.info("Attempting to send mail to " + to + " with link " + verificationLink);
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.mailgun.net/v3/" + mailgunDomain + "/messages").newBuilder();
        String url = urlBuilder.build().toString();

        String htmlContent = String.format(
                "<html>" +
                        "<head><style>" +
                        "  body {font-family: Arial, sans-serif; margin: 0; padding: 20px; color: #333;}" +
                        "  .container {background-color: #f8f8f8; border: 1px solid #e7e7e7; padding: 20px;}" +
                        "  h1 {color: #0056b3;}" +
                        "  a.button {background-color: #0056b3; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 5px;}" +
                        "  .footer {margin-top: 20px; font-size: 12px; text-align: center; color: #999;}" +
                        "</style></head>" +
                        "<body>" +
                        "  <div class='container'>" +
                        "    <h1>Verify Your Email for CSYE6225</h1>" +
                        "    <p>Dear user,</p>" +
                        "    <p>Thank you for registering with us. To complete your registration, please click the button below to verify your email address.</p>" +
                        "    <p><a href='%s' class='button'>Verify Email</a></p>" +
                        "    <p>If the button above does not work, copy and paste the following link in your browser:</p>" +
                        "    <p><a href='%s'>%s</a></p>" +
                        "  </div>" +
                        "  <div class='footer'>" +
                        "    <p>This is an automated message from CSYE6225. Please do not reply to this email.</p>" +
                        "  </div>" +
                        "</body></html>",
                verificationLink, verificationLink, verificationLink
        );

// The rest of the code remains unchanged.


        RequestBody formBody = new FormBody.Builder()
                .add("from", "CSYE6225 <postmaster@" + mailgunDomain + ">")
                .add("to", to)
                .add("subject", "Verify Your Email")
                .add("text", "Click here to verify your email: " + verificationLink)
                .add("html", htmlContent)
                .build();
        logger.info(formBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Authorization", Credentials.basic("api", mailgunApiKey))
                .build();
        logger.info(request.toString());
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.severe("Unexpected code " + response);
            }
            logger.log(Level.INFO, "Body: " + response.body().string());
        } catch (IOException e) {
            logger.severe("Error sending email: " + e.getMessage());
        }
    }
}
