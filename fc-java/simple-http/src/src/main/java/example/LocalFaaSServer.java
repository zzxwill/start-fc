package example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import com.aliyun.fc.runtime.PojoRequestHandler;

@SpringBootApplication
public class LocalFaaSServer {

    public static void main(String[] args) throws Exception {
        // Create the Spring application context
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(LocalFaaSServer.class);

        // Create and configure Jetty server
        Server server = new Server(8080);

        // Create servlet context handler
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());

        // Add Spring context loader listener
        servletContextHandler.addEventListener(new ContextLoaderListener(context));

        // Create and add Spring dispatcher servlet
        ServletHolder dispatcherServlet = new ServletHolder("dispatcher", new DispatcherServlet(context));
        servletContextHandler.addServlet(dispatcherServlet, "/*");

        server.setHandler(servletContextHandler);

        // Start the server
        server.start();
        server.join();
    }

    @Bean
    public PojoRequestHandler<?, ?> appHandler() {
        return new App();
    }

    @Bean
    public GenericController<?, ?> genericController(PojoRequestHandler<?, ?> appHandler) {
        return new GenericController<>(appHandler);
    }
}