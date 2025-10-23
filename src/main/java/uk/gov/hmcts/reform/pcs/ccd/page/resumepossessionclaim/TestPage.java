package uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim;

import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.pcs.ccd.common.CcdPageConfiguration;
import uk.gov.hmcts.reform.pcs.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;

import java.util.List;

public class TestPage implements CcdPageConfiguration {

    @Override
    public void addTo(PageBuilder pageBuilder) {
        pageBuilder
            .page("testPage")
            .pageLabel("Prohibited conduct standard contract")
            .label("testPage-info", """
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
            .mandatory(PCSCase::getQuestion2, "question1=\"YES\"")
            .mandatory(PCSCase::getDetailsOfTerms, "question2=\"YES\"")
            .mandatory(PCSCase::getWhyMakingClaim, "question1=\"YES\"");
    }

    private AboutToStartOrSubmitResponse<PCSCase, State> midEvent(CaseDetails<PCSCase, State> details,
                                                                  CaseDetails<PCSCase, State> detailsBefore) {

        return AboutToStartOrSubmitResponse.<PCSCase, State>builder()
            .errors(List.of("You're not eligible for this online service"))
            .build();
    }

}
