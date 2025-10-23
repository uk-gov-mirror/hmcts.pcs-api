package uk.gov.hmcts.reform.pcs.ccd.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Event.EventBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pcs.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.TestPage;

import static uk.gov.hmcts.reform.pcs.ccd.event.EventId.testEvent1;

@Slf4j
@Component
@AllArgsConstructor
public class TestEvent1 implements CCDConfig<PCSCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PCSCase, State, UserRole> configBuilder) {
        EventBuilder<PCSCase, UserRole, State> eventBuilder =
            configBuilder
                .decentralisedEvent(testEvent1.name(), this::submit)
                .forAllStates()
                .name("Test screen 1")
                .grant(Permission.CRUD, UserRole.PCS_SOLICITOR)
                .showSummary();

        new PageBuilder(eventBuilder)
            .add(new TestPage());

    }

    private SubmitResponse submit(EventPayload<PCSCase, State> eventPayload) {
        return SubmitResponse.builder().build();
    }

}
