package uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim;

import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.pcs.ccd.common.CcdPageConfiguration;
import uk.gov.hmcts.reform.pcs.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;
import uk.gov.hmcts.reform.pcs.ccd.domain.WrappedQuestion;

import java.util.List;

public class TestPage2 implements CcdPageConfiguration {

    @Override
    public void addTo(PageBuilder pageBuilder) {
        pageBuilder
            .page("testPage2")
            .pageLabel("Prohibited conduct standard contract")
            .label("testPage2-info", """
---
<p class="govuk-body govuk-!-font-size-19">
If a judge decides that possession is not reasonable at this time, they may instead decide to make an
order imposing a prohibited conduct standard contract.
</p>
<p class="govuk-body govuk-!-font-size-19">
This is a 12-month probationary contract
</p>
            """)
            .mandatory(PCSCase::getQuestion1)
            .complex(PCSCase::getWrappedQuestion, "question1=\"YES\"")
            .mandatory(WrappedQuestion::getQuestion2)
            .mandatory(WrappedQuestion::getDetailsOfTerms, "wrappedQuestion.question2=\"YES\"")
            .done()
            .mandatory(PCSCase::getWhyMakingClaim, "question1=\"YES\"");
    }

    private AboutToStartOrSubmitResponse<PCSCase, State> midEvent(CaseDetails<PCSCase, State> details,
                                                                  CaseDetails<PCSCase, State> detailsBefore) {

        return AboutToStartOrSubmitResponse.<PCSCase, State>builder()
            .errors(List.of("You're not eligible for this online service"))
            .build();
    }

}
