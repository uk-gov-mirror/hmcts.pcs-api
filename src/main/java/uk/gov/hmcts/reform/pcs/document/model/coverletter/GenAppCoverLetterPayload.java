package uk.gov.hmcts.reform.pcs.document.model.coverletter;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.docassembly.domain.FormPayload;

/**
 * Docmosis merge-field payload for the gen-app pack cover letter. Rendered at send time and placed between
 * the coversheet and the generated gen-app form. Field names must match the merge fields in
 * {@code CV-PCS-LET-ENG-Covering-Letter.docx}.
 */
@Data
@Builder
public class GenAppCoverLetterPayload implements FormPayload {

    private String courtName;
    private String courtAddressLine1;
    private String courtAddressLine2;
    private String courtPostcode;

    private String recipientName;
    private String recipientPersonalName;
    private String recipientOrganisationName;
    private String recipientAddressLine1;
    private String recipientAddressLine2;
    private String recipientPostTown;
    private String recipientCounty;
    private String recipientPostcode;

    private String caseName;
    private String caseReference;

    private String letterDate;
    private String signOff;

}
