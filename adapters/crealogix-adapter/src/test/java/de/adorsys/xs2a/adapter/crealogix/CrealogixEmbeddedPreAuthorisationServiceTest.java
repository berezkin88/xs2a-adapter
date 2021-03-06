package de.adorsys.xs2a.adapter.crealogix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.ResponseHeaders;
import de.adorsys.xs2a.adapter.api.http.HttpClient;
import de.adorsys.xs2a.adapter.api.http.Request;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import de.adorsys.xs2a.adapter.api.model.EmbeddedPreAuthorisationRequest;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import de.adorsys.xs2a.adapter.impl.security.AccessTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CrealogixEmbeddedPreAuthorisationServiceTest {

    private final HttpClient httpClient = mock(HttpClient.class);
    private final Aspsp aspsp = getAspsp();
    private CrealogixEmbeddedPreAuthorisationService authorisationService;

    @BeforeEach
    void setUp() {
        authorisationService
            = new CrealogixEmbeddedPreAuthorisationService(CrealogixClient.DKB, aspsp, httpClient);
    }

    @Test
    void getToken() throws IOException {
        Response tppResponse = mock(Response.class);
        Response psd2Response = mock(Response.class);
        TokenResponse tppTokenResponse = new TokenResponse();
        tppTokenResponse.setAccessToken("tpp");
        TokenResponse psd2TokenResponse = new TokenResponse();
        psd2TokenResponse.setAccessToken("psd2");

        Request.Builder tppBuilder = mock(Request.Builder.class);
        Request.Builder psd2Builder = mock(Request.Builder.class);

        doReturn(tppBuilder).when(httpClient).post(eq("https://localhost:8443/token"));
        doReturn(tppBuilder).when(tppBuilder).urlEncodedBody(anyMap());
        doReturn(tppBuilder).when(tppBuilder).headers(anyMap());
        doReturn(tppResponse).when(tppBuilder).send(any());
        doReturn(tppTokenResponse).when(tppResponse).getBody();

        doReturn(psd2Builder).when(httpClient).post(eq("https://localhost:8443/pre-auth/1.0.6/psd2-auth/v1/auth/token"));
        doReturn(psd2Builder).when(psd2Builder).jsonBody(anyString());
        doReturn(psd2Builder).when(psd2Builder).headers(anyMap());
        doReturn(psd2Response).when(psd2Builder).send(any());
        doReturn(psd2TokenResponse).when(psd2Response).getBody();

        TokenResponse token = authorisationService.getToken(new EmbeddedPreAuthorisationRequest(), RequestHeaders.fromMap(Collections.emptyMap()));

        CrealogixAuthorisationToken authorisationToken = CrealogixAuthorisationToken.decode(token.getAccessToken());

        assertThat(token.getAccessToken()).isNotBlank();
        assertThat(authorisationToken.getTppToken()).isEqualTo("tpp");
        assertThat(authorisationToken.getPsd2AuthorisationToken()).isEqualTo("psd2");
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 202})
    void responseHandler_successfulStatuses(int statusCode) throws JsonProcessingException {
        String stringBody = new ObjectMapper().writeValueAsString(new TokenResponse());
        InputStream inputStream = new ByteArrayInputStream(stringBody.getBytes());

        TokenResponse actualResponse
            = authorisationService.responseHandler().apply(statusCode, inputStream, ResponseHeaders.emptyResponseHeaders());

        assertThat(actualResponse)
            .isNotNull()
            .isInstanceOf(TokenResponse.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {302, 403, 500})
    void responseHandler_notSuccessfulStatuses(int statusCode) {
        HttpClient.ResponseHandler<TokenResponse> responseHandler = authorisationService.responseHandler();
        ResponseHeaders responseHeaders = ResponseHeaders.emptyResponseHeaders();

        assertThatThrownBy(() -> responseHandler.apply(statusCode, null, responseHeaders))
            .isInstanceOf(AccessTokenException.class);
    }

    private Aspsp getAspsp() {
        String url = "https://localhost:8443/";
        Aspsp aspsp = new Aspsp();
        aspsp.setIdpUrl(url);
        return aspsp;
    }
}
