package br.com.jmmarca.core.utils;

public final class PaginationUtil {

  private static final java.lang.String HEADER_X_TOTAL_COUNT = "X-Total-Count";

  private static final java.lang.String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

  private PaginationUtil() {
  }

  public static <T> org.springframework.http.HttpHeaders generatePaginationHttpHeaders(
      org.springframework.web.util.UriComponentsBuilder uriBuilder, org.springframework.data.domain.Page<T> page) {
    return null;
  }

  private static java.lang.String prepareLink(org.springframework.web.util.UriComponentsBuilder uriBuilder,
      int pageNumber, int pageSize, java.lang.String relType) {
    return null;
  }

  private static java.lang.String preparePageUri(org.springframework.web.util.UriComponentsBuilder uriBuilder,
      int pageNumber, int pageSize) {
    return null;
  }
}