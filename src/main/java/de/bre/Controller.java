package de.bre;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/postbox")
public class Controller {

    @Inject
    TelegramActuator telegramActuator;

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        log.info("API aufgerufen");
        telegramActuator.notifyUser();

        return Response.status(200).build();
    }
}