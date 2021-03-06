package de.adorsys.xs2a.adapter.api.http;

public interface HttpClientFactory {

    HttpClient getHttpClient(String adapterId, String qwacAlias, String[] supportedCipherSuites);

    default HttpClient getHttpClient(String adapterId, String qwacAlias) {
        return getHttpClient(adapterId, qwacAlias, null);
    }

    default HttpClient getHttpClient(String adapterId) {
        return getHttpClient(adapterId, null);
    }

    /**
     * @return An object with all necessary configurations for creating an {@link HttpClient}
     */
    HttpClientConfig getHttpClientConfig();
}
