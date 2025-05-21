package uk.gov.hmcts.reform.pcs.docassembly.service.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "document-assembly", url = "${document-assembly.api-url}")
public interface DocumentAssemblyApi {

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    String WELCOME_ENDPOINT = "/";

    @GetMapping(WELCOME_ENDPOINT)
    String getWelcomeMessage(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization
    );
} 