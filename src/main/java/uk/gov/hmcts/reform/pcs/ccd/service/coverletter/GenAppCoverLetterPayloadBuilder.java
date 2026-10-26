package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyEntity;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.location.model.CourtVenue;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Builds the gen-app cover letter payload from already-resolved recipient, court and case details.
 * Entity access here is limited to scalar fields so it stays safe outside a transaction.
 */
@Service
public class GenAppCoverLetterPayloadBuilder {

    private static final String SIGN_OFF = "Yours sincerely";

    public GenAppCoverLetterPayload build(CourtVenue court, PartyEntity recipient, String recipientName,
                                          AddressUK address, String caseName, String caseReference,
                                          String letterDate) {
        String[] courtAddressLines = splitLines(court == null ? null : court.courtAddress());
        return GenAppCoverLetterPayload.builder()
            .courtName(court == null ? null : court.courtName())
            .courtAddressLine1(courtAddressLines.length > 0 ? courtAddressLines[0] : null)
            .courtAddressLine2(courtAddressLines.length > 1 ? courtAddressLines[1] : null)
            .courtPostcode(court == null ? null : court.postcode())
            .recipientName(recipientName)
            .recipientPersonalName(personalName(recipient))
            .recipientOrganisationName(StringUtils.trimToNull(recipient.getOrgName()))
            .recipientAddressLine1(address.getAddressLine1())
            .recipientAddressLine2(address.getAddressLine2())
            .recipientPostTown(address.getPostTown())
            .recipientCounty(address.getCounty())
            .recipientPostcode(address.getPostCode())
            .caseName(caseName)
            .caseReference(caseReference)
            .letterDate(letterDate)
            .signOff(SIGN_OFF)
            .build();
    }

    private String personalName(PartyEntity recipient) {
        return Stream.of(recipient.getFirstName(), recipient.getLastName())
            .filter(StringUtils::isNotBlank)
            .reduce((a, b) -> a + " " + b)
            .orElse(null);
    }

    // Court address comes back as a single comma-separated string; first part is line 1, the rest line 2.
    private String[] splitLines(String courtAddress) {
        if (StringUtils.isBlank(courtAddress)) {
            return new String[0];
        }
        String[] parts = Arrays.stream(courtAddress.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .toArray(String[]::new);
        if (parts.length <= 1) {
            return parts;
        }
        String line2 = String.join(", ", Arrays.copyOfRange(parts, 1, parts.length));
        return new String[]{parts[0], line2};
    }
}
