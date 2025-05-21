package uk.gov.hmcts.reform.pcs.docassembly.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.pcs.docassembly.service.api.DocumentAssemblyApi;

@RequiredArgsConstructor
@Service
public class DocumentAssemblyService {

    private final DocumentAssemblyApi documentAssemblyApi;
    private final AuthTokenGenerator authTokenGenerator;

    public String getWelcomeMessage(@RequestHeader(AUTHORIZATION) String authorisation) {
        return documentAssemblyApi.getWelcomeMessage(authorisation, authTokenGenerator.generate());
    }
} 