package uk.gov.hmcts.reform.pcs.docstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pcs.docstore.service.api.DocStoreHealthApi;

@Service
@RequiredArgsConstructor
public class DocStoreHealthService {
    private final DocStoreHealthApi docStoreHealthApi;

    public String getHealthStatus() {
        return docStoreHealthApi.getHealth();
    }
} 