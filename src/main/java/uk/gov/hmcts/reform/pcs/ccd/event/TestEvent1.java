package uk.gov.hmcts.reform.pcs.ccd.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pcs.ccd.domain.CarType;
import uk.gov.hmcts.reform.pcs.ccd.domain.DefendantDetails;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;

import static uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole.PCS_CASE_WORKER;

@Component
@AllArgsConstructor
public class TestEvent1 implements CCDConfig<PCSCase, State, UserRole> {

    @Override
    public void configure(ConfigBuilder<PCSCase, State, UserRole> configBuilder) {
        configBuilder
        .event("nestedComplexTypes")
        .initialState(State.CASE_ISSUED)
        .name("Nested complex types")
        .grant(Permission.CRU, PCS_CASE_WORKER)
        .fields()
        .page("testPage1")
        .pageLabel("Test Page 1")
        .complex(PCSCase::getDefendant1)
            .mandatory(DefendantDetails::getFirstName)
                .complex(DefendantDetails::getCarType)
                .mandatoryWithLabel(CarType::getMake, "Car make label override")
                .optional(CarType::getModel)
            .done()
//            .mandatory(DefendantDetails::getCarType)
            .mandatory(DefendantDetails::getLastName)
        .done();

    }

}
