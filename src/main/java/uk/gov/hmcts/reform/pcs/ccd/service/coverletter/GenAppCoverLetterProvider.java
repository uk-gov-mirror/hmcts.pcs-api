package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.reform.pcs.ccd.entity.PcsCaseEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyEntity;
import uk.gov.hmcts.reform.pcs.ccd.service.bulkprint.LetterDocumentFetcher;
import uk.gov.hmcts.reform.pcs.ccd.service.document.DocumentIdExtractor;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.location.model.CourtVenue;
import uk.gov.hmcts.reform.pcs.location.service.LocationReferenceService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Renders the gen-app pack cover letter for a recipient and returns its PDF bytes: resolve the serving court,
 * build the payload, generate the PDF via Docmosis, then fetch its bytes from CDAM. The bytes are merged
 * directly after the coversheet by the bulk-print pack assembler.
 */
@Service
public class GenAppCoverLetterProvider {

    private static final DateTimeFormatter LETTER_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK);

    private final GenAppCoverLetterPayloadBuilder payloadBuilder;
    private final GenAppCoverLetterDocumentGenerator documentGenerator;
    private final LocationReferenceService locationReferenceService;
    private final DocumentIdExtractor documentIdExtractor;
    private final LetterDocumentFetcher letterDocumentFetcher;
    private final Clock ukClock;

    public GenAppCoverLetterProvider(GenAppCoverLetterPayloadBuilder payloadBuilder,
                                     GenAppCoverLetterDocumentGenerator documentGenerator,
                                     LocationReferenceService locationReferenceService,
                                     DocumentIdExtractor documentIdExtractor,
                                     LetterDocumentFetcher letterDocumentFetcher,
                                     @Qualifier("ukClock") Clock ukClock) {
        this.payloadBuilder = payloadBuilder;
        this.documentGenerator = documentGenerator;
        this.locationReferenceService = locationReferenceService;
        this.documentIdExtractor = documentIdExtractor;
        this.letterDocumentFetcher = letterDocumentFetcher;
        this.ukClock = ukClock;
    }

    public byte[] render(PcsCaseEntity pcsCase, PartyEntity recipient, String recipientName,
                         AddressUK address, String caseName, String caseReference) {
        GenAppCoverLetterPayload payload = payloadBuilder.build(resolveServingCourt(pcsCase), recipient,
            recipientName, address, caseName, caseReference, LocalDate.now(ukClock).format(LETTER_DATE));
        String url = documentGenerator.generate(payload);
        return letterDocumentFetcher.fetchBytes(documentIdExtractor.extractDocumentId(url));
    }

    private CourtVenue resolveServingCourt(PcsCaseEntity pcsCase) {
        Integer epimsId = pcsCase.getCaseManagementLocation();
        if (epimsId == null) {
            return null;
        }
        List<CourtVenue> venues = locationReferenceService.getCourtVenues(List.of(epimsId));
        return venues == null || venues.isEmpty() ? null : venues.getFirst();
    }
}
