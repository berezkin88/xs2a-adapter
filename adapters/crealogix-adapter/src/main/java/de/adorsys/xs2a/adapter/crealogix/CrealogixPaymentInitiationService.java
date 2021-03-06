/*
 * Copyright 2018-2020 adorsys GmbH & Co KG
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

package de.adorsys.xs2a.adapter.crealogix;

import de.adorsys.xs2a.adapter.api.RequestHeaders;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.http.HttpClient;
import de.adorsys.xs2a.adapter.api.http.HttpLogSanitizer;
import de.adorsys.xs2a.adapter.api.link.LinksRewriter;
import de.adorsys.xs2a.adapter.api.model.*;
import de.adorsys.xs2a.adapter.crealogix.model.CrealogixPaymentInitiationWithStatusResponse;
import de.adorsys.xs2a.adapter.impl.BasePaymentInitiationService;
import org.mapstruct.factory.Mappers;

import static java.util.function.Function.identity;

public class CrealogixPaymentInitiationService extends BasePaymentInitiationService {

    private final CrealogixMapper mapper = Mappers.getMapper(CrealogixMapper.class);
    private final CrealogixRequestResponseHandlers requestResponseHandlers;

    public CrealogixPaymentInitiationService(Aspsp aspsp,
                                             HttpClient httpClient,
                                             LinksRewriter linksRewriter,
                                             HttpLogSanitizer logSanitizer) {
        super(aspsp, httpClient, linksRewriter, logSanitizer);
        this.requestResponseHandlers = new CrealogixRequestResponseHandlers(logSanitizer);
    }

    @Override
    public Response<PaymentInitationRequestResponse201> initiatePayment(PaymentService paymentService,
                                                                        PaymentProduct paymentProduct,
                                                                        RequestHeaders requestHeaders,
                                                                        RequestParams requestParams,
                                                                        Object body) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.initiatePayment(paymentService,
            paymentProduct,
            body,
            requestHeaders,
            requestParams,
            identity(),
            requestResponseHandlers.crealogixResponseHandler(PaymentInitationRequestResponse201.class));
    }

    @Override
    public Response<PaymentInitiationWithStatusResponse> getSinglePaymentInformation(PaymentProduct paymentProduct,
                                                                                     String paymentId,
                                                                                     RequestHeaders requestHeaders,
                                                                                     RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInformation(PaymentService.PAYMENTS,
            paymentProduct,
            paymentId,
            requestHeaders,
            requestParams,
            requestResponseHandlers.jsonResponseHandler(CrealogixPaymentInitiationWithStatusResponse.class))
                .map(mapper::toPaymentInitiationWithStatusResponse);
    }

    @Override
    public Response<PeriodicPaymentInitiationWithStatusResponse> getPeriodicPaymentInformation(PaymentProduct paymentProduct,
                                                                                               String paymentId,
                                                                                               RequestHeaders requestHeaders,
                                                                                               RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPeriodicPaymentInformation(paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<PeriodicPaymentInitiationMultipartBody> getPeriodicPain001PaymentInformation(PaymentProduct paymentProduct,
                                                                                                 String paymentId,
                                                                                                 RequestHeaders requestHeaders,
                                                                                                 RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPeriodicPain001PaymentInformation(paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<String> getPaymentInformationAsString(PaymentService paymentService,
                                                          PaymentProduct paymentProduct,
                                                          String paymentId,
                                                          RequestHeaders requestHeaders,
                                                          RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInformationAsString(paymentService, paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<ScaStatusResponse> getPaymentInitiationScaStatus(PaymentService paymentService,
                                                                     PaymentProduct paymentProduct,
                                                                     String paymentId,
                                                                     String authorisationId,
                                                                     RequestHeaders requestHeaders,
                                                                     RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInitiationScaStatus(paymentService,
            paymentProduct,
            paymentId,
            authorisationId,
            requestHeaders,
            requestParams);
    }

    @Override
    public Response<PaymentInitiationStatusResponse200Json> getPaymentInitiationStatus(PaymentService paymentService,
                                                                                       PaymentProduct paymentProduct,
                                                                                       String paymentId,
                                                                                       RequestHeaders requestHeaders,
                                                                                       RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInitiationStatus(paymentService, paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<String> getPaymentInitiationStatusAsString(PaymentService paymentService,
                                                               PaymentProduct paymentProduct,
                                                               String paymentId,
                                                               RequestHeaders requestHeaders,
                                                               RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInitiationStatusAsString(paymentService, paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<Authorisations> getPaymentInitiationAuthorisation(PaymentService paymentService,
                                                                      PaymentProduct paymentProduct,
                                                                      String paymentId,
                                                                      RequestHeaders requestHeaders,
                                                                      RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.getPaymentInitiationAuthorisation(paymentService, paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<StartScaprocessResponse> startPaymentAuthorisation(PaymentService paymentService,
                                                                       PaymentProduct paymentProduct,
                                                                       String paymentId,
                                                                       RequestHeaders requestHeaders,
                                                                       RequestParams requestParams) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.startPaymentAuthorisation(paymentService, paymentProduct, paymentId, requestHeaders, requestParams);
    }

    @Override
    public Response<StartScaprocessResponse> startPaymentAuthorisation(PaymentService paymentService,
                                                                       PaymentProduct paymentProduct,
                                                                       String paymentId,
                                                                       RequestHeaders requestHeaders,
                                                                       RequestParams requestParams,
                                                                       UpdatePsuAuthentication updatePsuAuthentication) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.startPaymentAuthorisation(paymentService,
            paymentProduct,
            paymentId,
            requestHeaders,
            requestParams,
            updatePsuAuthentication);
    }

    @Override
    public Response<UpdatePsuAuthenticationResponse> updatePaymentPsuData(PaymentService paymentService,
                                                                          PaymentProduct paymentProduct,
                                                                          String paymentId,
                                                                          String authorisationId,
                                                                          RequestHeaders requestHeaders,
                                                                          RequestParams requestParams,
                                                                          UpdatePsuAuthentication updatePsuAuthentication) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.updatePaymentPsuData(paymentService,
            paymentProduct,
            paymentId,
            authorisationId,
            requestHeaders,
            requestParams,
            updatePsuAuthentication);
    }

    @Override
    public Response<SelectPsuAuthenticationMethodResponse> updatePaymentPsuData(PaymentService paymentService,
                                                                                PaymentProduct paymentProduct,
                                                                                String paymentId,
                                                                                String authorisationId,
                                                                                RequestHeaders requestHeaders,
                                                                                RequestParams requestParams,
                                                                                SelectPsuAuthenticationMethod selectPsuAuthenticationMethod) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.updatePaymentPsuData(paymentService,
            paymentProduct,
            paymentId,
            authorisationId,
            requestHeaders,
            requestParams,
            selectPsuAuthenticationMethod);
    }

    @Override
    public Response<ScaStatusResponse> updatePaymentPsuData(PaymentService paymentService,
                                                            PaymentProduct paymentProduct,
                                                            String paymentId,
                                                            String authorisationId,
                                                            RequestHeaders requestHeaders,
                                                            RequestParams requestParams,
                                                            TransactionAuthorisation transactionAuthorisation) {
        requestResponseHandlers.crealogixRequestHandler(requestHeaders);

        return super.updatePaymentPsuData(paymentService,
            paymentProduct,
            paymentId,
            authorisationId,
            requestHeaders,
            requestParams,
            transactionAuthorisation);
    }
}
