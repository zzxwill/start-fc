package example;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.aliyun.fc.runtime.PojoRequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@RestController
@RequestMapping("/api")
public class GenericController<T, R> {

    private final PojoRequestHandler<T, R> appHandler;
    private final Class<T> requestClass;
    private final Class<R> responseClass;

    @SuppressWarnings("unchecked")
    public GenericController(PojoRequestHandler<T, R> appHandler) {
        this.appHandler = appHandler;

        // Get the actual type parameters from the PojoRequestHandler implementation
        Type[] interfaces = appHandler.getClass().getGenericInterfaces();
        for (Type iface : interfaces) {
            if (iface instanceof ParameterizedType &&
                    ((ParameterizedType) iface).getRawType().equals(PojoRequestHandler.class)) {
                ParameterizedType paramType = (ParameterizedType) iface;
                this.requestClass = (Class<T>) paramType.getActualTypeArguments()[0];
                this.responseClass = (Class<R>) paramType.getActualTypeArguments()[1];
                return;
            }
        }
        throw new IllegalArgumentException("Could not determine generic types");
    }

    @PostMapping(value = "/handle", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<R> handleRequest(@RequestBody String jsonRequest) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Use the stored request class type
            T typedRequest = mapper.readValue(jsonRequest, requestClass);
            R response = appHandler.handleRequest(typedRequest, new JettyContext());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error processing request: " + e.getMessage());
        }
    }
}
