package de.bre;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TelegramActuator implements Actuator {

  @Inject
  @ConfigProperty(name = "telegram.apiToken")
  String apiToken;

  @Inject
  @ConfigProperty(name = "telegram.chatId")
  String chatId;

  @Inject
  @ConfigProperty(name = "notify.message")
  String message;

  private static final Logger log = LoggerFactory.getLogger(TelegramActuator.class);

  private long lastHitOnApi;

  @Override
  public void notifyUser() {
    if (lastHitOnApiMoreThanOneMinuteAgo()) {
      sendTelegramMessage();
      log.info("Benachrichtigung versendet mit {}", this.getClass().getName());
    }
  }

  private boolean lastHitOnApiMoreThanOneMinuteAgo() {
    if (System.currentTimeMillis() - lastHitOnApi > TimeUnit.MINUTES.toMillis(1)) {
      lastHitOnApi = System.currentTimeMillis();
      return true;
    }
    return false;
  }

  private void sendTelegramMessage() {
    String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    urlString = String.format(urlString, apiToken, chatId, message);

    URL url;
    try {
      url = new URL(urlString);

      URLConnection conn = url.openConnection();

      StringBuilder sb = new StringBuilder();
      InputStream is = new BufferedInputStream(conn.getInputStream());

      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String inputLine;
      while ((inputLine = br.readLine()) != null) {
        sb.append(inputLine);
      }
      log.info("Versendete Nachricht: {}", sb);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

