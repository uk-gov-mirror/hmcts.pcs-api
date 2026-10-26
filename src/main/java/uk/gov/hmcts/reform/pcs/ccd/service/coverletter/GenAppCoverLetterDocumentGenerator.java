package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.docassembly.domain.OutputType;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.document.service.DocAssemblyService;

/**
 * Thin Docmosis wrapper for the gen-app pack cover letter. Rendered at send time and inserted after the
 * coversheet; not stored as a case document.
 */
@Service
public class GenAppCoverLetterDocumentGenerator {

    static final String TEMPLATE_ID = "CV-PCS-LET-ENG-Covering-Letter.docx";
    static final String OUTPUT_FILENAME = "GenAppCoverLetter";

    private final DocAssemblyService docAssemblyService;

    public GenAppCoverLetterDocumentGenerator(DocAssemblyService docAssemblyService) {
        this.docAssemblyService = docAssemblyService;
    }

    public String generate(GenAppCoverLetterPayload payload) {
        return docAssemblyService.generateDocument(payload, TEMPLATE_ID, OutputType.PDF, OUTPUT_FILENAME);
    }
}
