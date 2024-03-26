package gcfv2pubsub;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.pubsub.v1.Message;
import com.google.events.cloud.pubsub.v1.MessagePublishedData;
import com.google.gson.Gson;
import dto.PublishedRequestDTO;
import io.cloudevents.CloudEvent;
import service.DatabaseService;
import service.EmailService;

import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

public class PubSubFunction implements CloudEventsFunction {
  private static final Logger logger = Logger.getLogger(PubSubFunction.class.getName());

  @Override
  public void accept(CloudEvent event) {
    // Get cloud event data as JSON string
    String cloudEventData = new String(event.getData().toBytes());
    // Decode JSON event data to the Pub/Sub MessagePublishedData type
    Gson gson = new Gson();
    MessagePublishedData data = gson.fromJson(cloudEventData, MessagePublishedData.class);
    // Get the message from the data
    Message message = data.getMessage();
    // Get the base64-encoded data from the message & decode it
    String encodedData = message.getData();
    String decodedData = new String(Base64.getDecoder().decode(encodedData));
    gson = new Gson();
    PublishedRequestDTO publishedRequestDTO = gson.fromJson(decodedData, PublishedRequestDTO.class);
    // Log the message
    logger.info("Pub/Sub message: " + publishedRequestDTO);
    String token = generateToken(publishedRequestDTO.getEmail());
    new DatabaseService().updateExpiryTime(publishedRequestDTO.getEmail(), token);
    new EmailService().sendEmail(publishedRequestDTO.getEmail(), token);
  }

  private String generateToken(String email){
    String uuid = UUID.randomUUID().toString();
    String token = Base64.getEncoder().encodeToString((email + ":" + uuid).getBytes());
    logger.info("Generated token: " + token);
    return token;
  }

}