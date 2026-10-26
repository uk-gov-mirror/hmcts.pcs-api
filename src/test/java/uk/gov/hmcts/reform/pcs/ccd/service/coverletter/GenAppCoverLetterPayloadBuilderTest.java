package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyEntity;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.location.model.CourtVenue;

import static org.assertj.core.api.Assertions.assertThat;

class GenAppCoverLetterPayloadBuilderTest {

    private final GenAppCoverLetterPayloadBuilder underTest = new GenAppCoverLetterPayloadBuilder();

    private final AddressUK address = AddressUK.builder()
        .addressLine1("1 High Street")
        .addressLine2("Flat 2")
        .postTown("London")
        .county("Greater London")
        .postCode("W1 1AA")
        .build();

    @Test
    @DisplayName("Maps court, recipient, case and letter fields onto the payload")
    void shouldMapAllFields() {
        PartyEntity recipient = PartyEntity.builder()
            .firstName("Bob").lastName("Tenant").orgName("Acme Ltd").build();

        GenAppCoverLetterPayload payload = underTest.build(
            courtVenue("Central London County Court", "Thomas More Building, Royal Courts", "WC2A 2LL"),
            recipient, "Bob Tenant", address, "Acme Ltd v Bob Tenant", "1234-5678-9012-3456", "26 October 2026");

        assertThat(payload.getCourtName()).isEqualTo("Central London County Court");
        assertThat(payload.getCourtAddressLine1()).isEqualTo("Thomas More Building");
        assertThat(payload.getCourtAddressLine2()).isEqualTo("Royal Courts");
        assertThat(payload.getCourtPostcode()).isEqualTo("WC2A 2LL");
        assertThat(payload.getRecipientName()).isEqualTo("Bob Tenant");
        assertThat(payload.getRecipientPersonalName()).isEqualTo("Bob Tenant");
        assertThat(payload.getRecipientOrganisationName()).isEqualTo("Acme Ltd");
        assertThat(payload.getRecipientAddressLine1()).isEqualTo("1 High Street");
        assertThat(payload.getRecipientAddressLine2()).isEqualTo("Flat 2");
        assertThat(payload.getRecipientPostTown()).isEqualTo("London");
        assertThat(payload.getRecipientCounty()).isEqualTo("Greater London");
        assertThat(payload.getRecipientPostcode()).isEqualTo("W1 1AA");
        assertThat(payload.getCaseName()).isEqualTo("Acme Ltd v Bob Tenant");
        assertThat(payload.getCaseReference()).isEqualTo("1234-5678-9012-3456");
        assertThat(payload.getLetterDate()).isEqualTo("26 October 2026");
        assertThat(payload.getSignOff()).isEqualTo("Yours sincerely");
    }

    @Test
    @DisplayName("Puts a single-part court address on line 1 and leaves line 2 null")
    void shouldHandleSingleLineCourtAddress() {
        GenAppCoverLetterPayload payload = underTest.build(
            courtVenue("County Court", "1 Court Road", "AB1 2CD"),
            PartyEntity.builder().firstName("Bob").build(), "Bob", address, "x v y", "ref", "date");

        assertThat(payload.getCourtAddressLine1()).isEqualTo("1 Court Road");
        assertThat(payload.getCourtAddressLine2()).isNull();
    }

    @Test
    @DisplayName("Leaves all court fields null when no serving court is resolved")
    void shouldHandleNullCourt() {
        GenAppCoverLetterPayload payload = underTest.build(
            null, PartyEntity.builder().firstName("Bob").build(), "Bob", address, "x v y", "ref", "date");

        assertThat(payload.getCourtName()).isNull();
        assertThat(payload.getCourtAddressLine1()).isNull();
        assertThat(payload.getCourtAddressLine2()).isNull();
        assertThat(payload.getCourtPostcode()).isNull();
    }

    @Test
    @DisplayName("Derives a null personal name and organisation name when the party has neither")
    void shouldHandleMissingNames() {
        GenAppCoverLetterPayload payload = underTest.build(
            courtVenue("Court", "1 Road", "AB1 2CD"),
            PartyEntity.builder().build(), "Persons unknown", address, "x v y", "ref", "date");

        assertThat(payload.getRecipientPersonalName()).isNull();
        assertThat(payload.getRecipientOrganisationName()).isNull();
        assertThat(payload.getRecipientName()).isEqualTo("Persons unknown");
    }

    private CourtVenue courtVenue(String courtName, String courtAddress, String postcode) {
        return new CourtVenue(
            null, null, null, null, null, null, null, null, null, null,
            courtAddress,
            postcode,
            null, null, null, null, null, null, null, null,
            courtName,
            null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null
        );
    }
}
