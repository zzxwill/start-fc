package example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import com.aliyun.fc.runtime.PojoRequestHandler;

public class JettyServer<REQ, RESP> {
    private final PojoRequestHandler<REQ, RESP> pojoHandler;

    public JettyServer(PojoRequestHandler<REQ, RESP> handler) {
        this.pojoHandler = handler;
    }

    public void start() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest,
                    HttpServletRequest request, HttpServletResponse response) {
                try {
                    // Convert servlet request to generic request type
                    REQ req = convertToRequest(request);

                    // Handle using POJO handler
                    RESP result = pojoHandler.handleRequest(req, new JettyContext());

                    // Set response
                    writeResponse(response, result);
                    baseRequest.setHandled(true);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        server.start();
        server.join();
    }

    // These methods would need to be implemented based on your specific REQ/RESP
    // types
    protected REQ convertToRequest(HttpServletRequest request) throws Exception {
        // Implementation depends on REQ type
        throw new UnsupportedOperationException("Must implement convertToRequest");
    }

    protected void writeResponse(HttpServletResponse response, RESP result) throws Exception {
        // Implementation depends on RESP type
        throw new UnsupportedOperationException("Must implement writeResponse");
    }

    public static void main(String[] args) throws Exception {
        // Example usage with specific types:
        new JettyServer<HTTPTriggerEvent, HTTPTriggerResponse>(new App()).start();
    }
}
