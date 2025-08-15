package uk.gov.hmcts.reform.pcs.ccd.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;

import static uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole.PCS_CASE_WORKER;

@Component
@Slf4j
@AllArgsConstructor
public class UrlParamTestEvent implements CCDConfig<PCSCase, State, UserRole> {

    @Override
    public void configure(final ConfigBuilder<PCSCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent("urlParamTestEvent", this::submit, this::start)
            .forAllStates()
            .name("Test URL params")
//            .showCondition(ShowConditions.NEVER_SHOW)
            .grant(Permission.CRU, PCS_CASE_WORKER)
            .fields()
            .page("page-1")
                .readonly(PCSCase::getTestEventMarkdown)
            .done();
    }

    // TODO: Can we use Jackson @type (or something based on that) to handle subclasses?

    private PCSCase start(EventPayload<PCSCase, State> eventPayload) {
        PCSCase caseData = eventPayload.caseData();

        String claimIdParam = eventPayload.urlParams().getFirst("claimId");

        caseData.setTestEventMarkdown("## Claim ID parameter is " + claimIdParam);

        return caseData;
    }

    private void submit(EventPayload<PCSCase, State> eventPayload) {
    }

}
