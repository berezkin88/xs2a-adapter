/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.xs2a.adapter.deutschebank;

import de.adorsys.xs2a.adapter.api.AccountInformationServiceProvider;
import de.adorsys.xs2a.adapter.api.PaymentInitiationServiceProvider;
import de.adorsys.xs2a.adapter.api.http.HttpClientConfig;
import de.adorsys.xs2a.adapter.api.http.HttpClientFactory;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceLoaderTest {
    private static final Aspsp ASPSP = buildAspspWithUrl();
    private HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
    private final HttpClientConfig httpClientConfig = mock(HttpClientConfig.class);;

    @BeforeEach
    void setUp() {
        when(httpClientFactory.getHttpClientConfig()).thenReturn(httpClientConfig);
    }

    @Test
    void getPaymentInitiationServiceProvider() {
        ServiceLoader<PaymentInitiationServiceProvider> loader = ServiceLoader.load(PaymentInitiationServiceProvider.class);
        PaymentInitiationServiceProvider provider = loader.iterator().next();

        assertThat(provider).isInstanceOf(DeutscheBankServiceProvider.class);
        assertThat(provider.getPaymentInitiationService(ASPSP, httpClientFactory, null)).isNotNull();
    }

    @Test
    void getAccountInformationServiceProvider() {
        ServiceLoader<AccountInformationServiceProvider> loader = ServiceLoader.load(AccountInformationServiceProvider.class);
        AccountInformationServiceProvider provider = loader.iterator().next();

        assertThat(provider).isInstanceOf(DeutscheBankServiceProvider.class);
        assertThat(provider.getAccountInformationService(ASPSP, httpClientFactory, null)).isNotNull();
    }

    private static Aspsp buildAspspWithUrl() {
        Aspsp aspsp = new Aspsp();
        aspsp.setUrl("url");
        return aspsp;
    }
}
