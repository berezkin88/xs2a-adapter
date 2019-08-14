package de.adorsys.xs2a.adapter.service.impl;

import de.adorsys.xs2a.adapter.service.AspspReadOnlyRepository;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.ais.AccountInformationService;
import de.adorsys.xs2a.adapter.service.exception.AdapterNotFoundException;
import de.adorsys.xs2a.adapter.service.exception.AspspRegistrationNotFoundException;
import de.adorsys.xs2a.adapter.service.model.Aspsp;
import de.adorsys.xs2a.adapter.service.provider.AccountInformationServiceProvider;
import de.adorsys.xs2a.adapter.service.provider.AdapterServiceProvider;
import de.adorsys.xs2a.adapter.service.provider.PaymentInitiationServiceProvider;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

public class AdapterServiceLoader {
    private final AspspReadOnlyRepository aspspRepository;
    private final ConcurrentMap<Class<?>, ServiceLoader<? extends AdapterServiceProvider>> serviceLoaders = new ConcurrentHashMap<>();

    public AdapterServiceLoader(AspspReadOnlyRepository aspspRepository) {
        this.aspspRepository = aspspRepository;
    }

    public AccountInformationService getAccountInformationService(RequestHeaders requestHeaders) {
        Aspsp aspsp = getAspsp(requestHeaders);
        String adapterId = aspsp.getAdapterId();
        String baseUrl = aspsp.getUrl();
        return getServiceProvider(AccountInformationServiceProvider.class, adapterId)
            .orElseThrow(() -> new AdapterNotFoundException(adapterId))
            .getAccountInformationService(baseUrl);
    }

    private Aspsp getAspsp(RequestHeaders requestHeaders) {
        Optional<String> aspspId = requestHeaders.get(RequestHeaders.X_GTW_ASPSP_ID);
        Optional<String> bankCode = requestHeaders.get(RequestHeaders.X_GTW_BANK_CODE);
        if (aspspId.isPresent()) {
            return aspspRepository.findById(aspspId.get())
                .orElseThrow(() -> new AspspRegistrationNotFoundException("No ASPSP was found with id: " + aspspId.get()));
        }
        if (bankCode.isPresent()) {
            List<Aspsp> aspsps = aspspRepository.findByBankCode(bankCode.get());
            if (aspsps.size() == 0) {
                throw new AspspRegistrationNotFoundException("No ASPSP was found with bank code: " + bankCode.get());
            } else if (aspsps.size() > 1) {
                throw new AspspRegistrationNotFoundException("The ASPSP could not be identified: " + aspsps.size()
                    + " registry entries found for bank code " + bankCode.get());
            }
            return aspsps.get(0);
        }
        throw new AspspRegistrationNotFoundException("Neither " + RequestHeaders.X_GTW_ASPSP_ID + " or "
            + RequestHeaders.X_GTW_BANK_CODE + " headers were provided to identify the ASPSP");
    }

    public <T extends AdapterServiceProvider> Optional<T> getServiceProvider(Class<T> klass, String adapterId) {
        ServiceLoader<T> serviceLoader = getServiceLoader(klass);

        return StreamSupport.stream(serviceLoader.spliterator(), false)
            .filter(provider -> provider.getAdapterId().equalsIgnoreCase(adapterId))
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    private <T extends AdapterServiceProvider> ServiceLoader<T> getServiceLoader(Class<T> klass) {
        return (ServiceLoader<T>) serviceLoaders.computeIfAbsent(klass, k -> ServiceLoader.load(klass));
    }

    public PaymentInitiationService getPaymentInitiationService(RequestHeaders requestHeaders) {
        Aspsp aspsp = getAspsp(requestHeaders);
        String adapterId = aspsp.getAdapterId();
        String baseUrl = aspsp.getUrl();
        return getServiceProvider(PaymentInitiationServiceProvider.class, adapterId)
            .orElseThrow(() -> new AdapterNotFoundException(adapterId))
            .getPaymentInitiationService(baseUrl);
    }
}