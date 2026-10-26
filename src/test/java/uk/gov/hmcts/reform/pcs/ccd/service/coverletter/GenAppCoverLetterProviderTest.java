package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.reform.pcs.ccd.entity.PcsCaseEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyEntity;
import uk.gov.hmcts.reform.pcs.ccd.service.bulkprint.LetterDocumentFetcher;
import uk.gov.hmcts.reform.pcs.ccd.service.document.DocumentIdExtractor;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.location.model.CourtVenue;
import uk.gov.hmcts.reform.pcs.location.service.LocationReferenceService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenAppCoverLetterProviderTest {

    @Mock
    private GenAppCoverLetterPayloadBuilder payloadBuilder;
    @Mock
    private GenAppCoverLetterDocumentGenerator documentGenerator;
    @Mock
    private LocationReferenceService locationReferenceService;
    @Mock
    private DocumentIdExtractor documentIdExtractor;
    @Mock
    private LetterDocumentFetcher letterDocumentFetcher;

    private GenAppCoverLetterProvider underTest;

    private final AddressUK address = AddressUK.builder().addressLine1("1 High Street").build();
    private final PartyEntity recipient = PartyEntity.builder().id(UUID.randomUUID()).build();
    private final GenAppCoverLetterPayload payload = GenAppCoverLetterPayload.builder().build();

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-26T10:00:00Z"), ZoneId.of("Europe/London"));
        underTest = new GenAppCoverLetterProvider(payloadBuilder, documentGenerator, locationReferenceService,
            documentIdExtractor, letterDocumentFetcher, fixedClock);
    }

    @Test
    @DisplayName("Resolves the serving court, builds the payload with today's date and returns the rendered bytes")
    void shouldRenderWithResolvedCourt() {
        byte[] bytes = "cover-letter".getBytes();
        UUID documentId = UUID.randomUUID();
        CourtVenue court = courtVenue("Central London County Court");
        final PcsCaseEntity pcsCase = PcsCaseEntity.builder().caseManagementLocation(123).build();
        when(locationReferenceService.getCourtVenues(List.of(123))).thenReturn(List.of(court));
        when(payloadBuilder.build(eq(court), eq(recipient), eq("Bob Tenant"), eq(address),
            eq("Acme v Bob"), eq("1234-5678-9012-3456"), eq("26 October 2026"))).thenReturn(payload);
        when(documentGenerator.generate(payload)).thenReturn("http://dm-store/documents/" + documentId);
        when(documentIdExtractor.extractDocumentId("http://dm-store/documents/" + documentId)).thenReturn(documentId);
        when(letterDocumentFetcher.fetchBytes(documentId)).thenReturn(bytes);

        byte[] result = underTest.render(
            pcsCase, recipient, "Bob Tenant", address, "Acme v Bob", "1234-5678-9012-3456");

        assertThat(result).isEqualTo(bytes);
    }

    @Test
    @DisplayName("Builds the payload with a null court when the case has no management location")
    void shouldRenderWithoutCourtWhenNoLocation() {
        final PcsCaseEntity pcsCase = PcsCaseEntity.builder().caseManagementLocation(null).build();
        when(payloadBuilder.build(eq(null), any(), any(), any(), any(), any(), any())).thenReturn(payload);
        when(documentGenerator.generate(payload)).thenReturn("url");
        when(documentIdExtractor.extractDocumentId("url")).thenReturn(UUID.randomUUID());
        when(letterDocumentFetcher.fetchBytes(any())).thenReturn("bytes".getBytes());

        underTest.render(pcsCase, recipient, "Bob", address, "cn", "ref");

        verifyNoInteractions(locationReferenceService);
        verify(payloadBuilder).build(eq(null), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Builds the payload with a null court when no venue is found for the location")
    void shouldRenderWithoutCourtWhenNoVenueFound() {
        final PcsCaseEntity pcsCase = PcsCaseEntity.builder().caseManagementLocation(999).build();
        when(locationReferenceService.getCourtVenues(List.of(999))).thenReturn(List.of());
        when(payloadBuilder.build(eq(null), any(), any(), any(), any(), any(), any())).thenReturn(payload);
        when(documentGenerator.generate(payload)).thenReturn("url");
        when(documentIdExtractor.extractDocumentId("url")).thenReturn(UUID.randomUUID());
        when(letterDocumentFetcher.fetchBytes(any())).thenReturn("bytes".getBytes());

        underTest.render(pcsCase, recipient, "Bob", address, "cn", "ref");

        verify(payloadBuilder).build(eq(null), any(), any(), any(), any(), any(), any());
        verify(locationReferenceService, never()).getCourtVenues(List.of());
    }

    private CourtVenue courtVenue(String courtName) {
        return new CourtVenue(
            null, null, null, null, null, null, null, null, null, null,
            "1 Court Road", "AB1 2CD",
            null, null, null, null, null, null, null, null,
            courtName,
            null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null
        );
    }
}
