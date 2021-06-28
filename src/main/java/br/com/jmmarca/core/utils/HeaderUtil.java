package br.com.jmmarca.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HeaderUtil {

    private final Logger log = LoggerFactory.getLogger(HeaderUtil.class);    

    private HeaderUtil() {
    }

    public static org.springframework.http.HttpHeaders createAlert(java.lang.String applicationName,
            java.lang.String message, java.lang.String param) {
        return null;
    }

    public static org.springframework.http.HttpHeaders createEntityCreationAlert(java.lang.String applicationName,
            boolean enableTranslation, java.lang.String entityName, java.lang.String param) {
        return null;
    }

    public static org.springframework.http.HttpHeaders createEntityUpdateAlert(java.lang.String applicationName,
            boolean enableTranslation, java.lang.String entityName, java.lang.String param) {
        return null;
    }

    public static org.springframework.http.HttpHeaders createEntityDeletionAlert(java.lang.String applicationName,
            boolean enableTranslation, java.lang.String entityName, java.lang.String param) {
        return null;
    }

    public static org.springframework.http.HttpHeaders createFailureAlert(java.lang.String applicationName,
            boolean enableTranslation, java.lang.String entityName, java.lang.String errorKey,
            java.lang.String defaultMessage) {
        return null;
    }

    static {
    }
    {
    }
}