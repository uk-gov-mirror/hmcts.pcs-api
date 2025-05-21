package uk.gov.hmcts.reform.pcs.docstore.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "doc-store", url = "${doc-store.api-url}")
public interface DocStoreHealthApi {
    @GetMapping("/health")
    String getHealth();
} 