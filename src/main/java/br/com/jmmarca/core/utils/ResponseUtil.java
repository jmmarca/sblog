package br.com.jmmarca.core.utils;

public abstract interface ResponseUtil {

    public static <X> org.springframework.http.ResponseEntity<X> wrapOrNotFound(java.util.Optional<X> maybeResponse) {
        return null;
    }

    public static <X> org.springframework.http.ResponseEntity<X> wrapOrNotFound(java.util.Optional<X> maybeResponse,
            org.springframework.http.HttpHeaders header) {
        return null;
    }

    private static org.springframework.http.ResponseEntity lambda$wrapOrNotFound$0(
            org.springframework.http.HttpHeaders header, java.lang.Object response) {
        return null;
    }
}