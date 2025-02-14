package work.vietdefi.clean.http;

import com.google.gson.JsonObject;
import io.vertx.core.*;
import work.vietdefi.util.json.GsonUtil;
import work.vietdefi.util.log.DebugLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * VertxServer is a singleton class responsible for starting and managing
 * a Vert.x application instance. It configures the Vert.x runtime and
 * deploys a specified verticle with appropriate settings.
 */
public class VertxServer {

    // Singleton instance of VertxServer
    private static VertxServer instance;

    // Vertx instance used to run the application
    public Vertx vertx;
    // Config for vertx server
    public JsonObject config;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the Vert.x server with default configurations.
     */
    private VertxServer() {
        try {
            String configFile = "config/http/vertx.json";
            String data = new String(Files.readAllBytes(Paths.get(configFile)), StandardCharsets.UTF_8);
            this.config = GsonUtil.gsonConverter.toJsonElement(data).getAsJsonObject();
        } catch (IOException e) {
            DebugLogger.logger.error("", e);
        }
    }

    /**
     * Retrieves the singleton instance of VertxServer.
     * If the instance doesn't exist, it creates one.
     *
     * @return the singleton instance of VertxServer
     */
    public static VertxServer getInstance() {
        if (instance == null) {
            instance = new VertxServer();
        }
        return instance;
    }

    /**
     * Starts the application by deploying the verticle.
     * @return A Future that completes when the deployment succeeds or fails.
     */
    public Future<String> start() {
        // Configure Vert.x options
        VertxOptions vxOptions = new VertxOptions()
                .setBlockedThreadCheckInterval(30000); // Set interval for blocked thread checks
        // Initialize the Vert.x instance
        vertx = Vertx.vertx(vxOptions);
        // Get the number of available processors.
        int procs = Runtime.getRuntime().availableProcessors();

        // Set up deployment options.
        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setThreadingModel(ThreadingModel.WORKER) // Use worker threading model
                .setWorkerPoolSize(procs * 2)             // Pool size = 2x CPU cores
                .setInstances(procs * 2);                 // Deploy instances = 2x CPU cores
        // Return a Future that completes when the deployment finishes.
        return vertx.deployVerticle(VertxVerticle.class.getName(), deploymentOptions)
                .onSuccess(id -> DebugLogger.logger.error("Your Vert.x application is started! ID = {}", id))
                .onFailure(err -> DebugLogger.logger.error("Unable to start your application", err));
    }
}
