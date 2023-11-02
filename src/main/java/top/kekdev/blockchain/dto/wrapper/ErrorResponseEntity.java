package top.kekdev.blockchain.dto.wrapper;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ErrorResponseEntity<T> extends ResponseEntity<Map<Object, Object>> {

    public ErrorResponseEntity(HttpStatusCode status) {
        super(Map.of("success", false), status);
    }

    public ErrorResponseEntity(T body, HttpStatusCode status) {
        super(Map.of("success", false, "error", body), status);
    }
}
