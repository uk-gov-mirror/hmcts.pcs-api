package uk.gov.hmcts.reform.pcs.testingsupport.endpoint;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.hmcts.reform.pcs.notify.model.EmailNotificationRequest;
import uk.gov.hmcts.reform.pcs.notify.service.NotificationService;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.hmcts.reform.pcs.docassembly.service.DocumentAssemblyService;
import uk.gov.hmcts.reform.pcs.docstore.service.DocStoreHealthService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/*
    This is a temporary endpoint created purely for testing the integration with Gov Notify, and will be removed once
    events are added to our service, DO NOT USE for any future events.
*/

@Slf4j
@RestController
@RequestMapping("/testing-support")
@ConditionalOnProperty(name = "testing-support.enabled", havingValue = "true")
@Tag(name = "Testing Support")
public class TestingSupportController {

    private final NotificationService notificationService;
    private final DocumentAssemblyService documentAssemblyService;
    private final DocStoreHealthService docStoreHealthService;

    public TestingSupportController(
        NotificationService notificationService,
        DocumentAssemblyService documentAssemblyService,
        DocStoreHealthService docStoreHealthService
    ) {
        this.notificationService = notificationService;
        this.documentAssemblyService = documentAssemblyService;
        this.docStoreHealthService = docStoreHealthService;
    }

    @PostMapping(value = "/send-email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SendEmailResponse> sendEmail(
        @RequestHeader(value = AUTHORIZATION, defaultValue = "DummyId") String authorisation,
        @RequestHeader(value = "ServiceAuthorization") String serviceAuthorization,
        @RequestBody EmailNotificationRequest emailRequest) {
        log.debug("Received request to send email to {}", emailRequest.getEmailAddress());

        SendEmailResponse notificationResponse = notificationService.sendEmail(emailRequest);

        return ResponseEntity.ok(notificationResponse);
    }

    @GetMapping(value = "/docassembly-welcome")
    public ResponseEntity<String> docAssemblyWelcome(
        @RequestHeader(value = AUTHORIZATION) String authorisation) {
        log.info("Testing connection to Document Assembly API welcome endpoint");
        return ResponseEntity.ok(documentAssemblyService.getWelcomeMessage(authorisation));
    }

    @GetMapping(value = "/doc-store-health")
    public ResponseEntity<String> docStoreHealth() {
        log.info("Testing connection to Document Management Store /health endpoint");
        return ResponseEntity.ok(docStoreHealthService.getHealthStatus());
    }
}
