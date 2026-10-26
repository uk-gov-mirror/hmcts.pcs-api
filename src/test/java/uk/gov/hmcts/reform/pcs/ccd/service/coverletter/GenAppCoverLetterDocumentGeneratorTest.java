package uk.gov.hmcts.reform.pcs.ccd.service.coverletter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.docassembly.domain.OutputType;
import uk.gov.hmcts.reform.pcs.document.model.coverletter.GenAppCoverLetterPayload;
import uk.gov.hmcts.reform.pcs.document.service.DocAssemblyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenAppCoverLetterDocumentGeneratorTest {

    @Mock
    private DocAssemblyService docAssemblyService;

    @Test
    @DisplayName("Generates the cover letter via Doc Assembly using the covering-letter template and returns its url")
    void shouldGenerateViaDocAssembly() {
        GenAppCoverLetterDocumentGenerator underTest = new GenAppCoverLetterDocumentGenerator(docAssemblyService);
        GenAppCoverLetterPayload payload = GenAppCoverLetterPayload.builder().build();
        when(docAssemblyService.generateDocument(payload, "CV-PCS-LET-ENG-Covering-Letter.docx",
            OutputType.PDF, "GenAppCoverLetter")).thenReturn("http://dm-store/documents/123");

        assertThat(underTest.generate(payload)).isEqualTo("http://dm-store/documents/123");
    }
}
