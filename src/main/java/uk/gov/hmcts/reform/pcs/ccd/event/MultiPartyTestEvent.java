package uk.gov.hmcts.reform.pcs.ccd.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Event.EventBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.pcs.ccd.ShowConditions;
import uk.gov.hmcts.reform.pcs.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pcs.ccd.domain.ClaimantType;
import uk.gov.hmcts.reform.pcs.ccd.domain.DefendantDetails;
import uk.gov.hmcts.reform.pcs.ccd.domain.PCSCase;
import uk.gov.hmcts.reform.pcs.ccd.domain.Party;
import uk.gov.hmcts.reform.pcs.ccd.domain.PaymentStatus;
import uk.gov.hmcts.reform.pcs.ccd.domain.State;
import uk.gov.hmcts.reform.pcs.ccd.domain.VerticalYesNo;
import uk.gov.hmcts.reform.pcs.ccd.entity.ClaimEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.ClaimGroundEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.PartyEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.PartyRole;
import uk.gov.hmcts.reform.pcs.ccd.entity.PcsCaseEntity;
import uk.gov.hmcts.reform.pcs.ccd.page.builder.SavingPageBuilderFactory;
import uk.gov.hmcts.reform.pcs.ccd.page.createpossessionclaim.ClaimantCircumstances;
import uk.gov.hmcts.reform.pcs.ccd.page.createpossessionclaim.MoneyJudgment;
import uk.gov.hmcts.reform.pcs.ccd.page.createpossessionclaim.RentArrearsOrBreachOfTenancyGround;
import uk.gov.hmcts.reform.pcs.ccd.page.createpossessionclaim.SecureOrFlexibleGroundsForPossession;
import uk.gov.hmcts.reform.pcs.ccd.page.createpossessionclaim.SecureOrFlexibleGroundsForPossessionReasons;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.CheckingNotice;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ClaimTypeNotEligibleEngland;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ClaimTypeNotEligibleWales;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ClaimantInformation;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ClaimantTypeNotEligibleEngland;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ClaimantTypeNotEligibleWales;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ContactPreferences;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.DailyRentAmount;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.DefendantsDetails;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.GroundForPossessionAdditionalGrounds;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.GroundForPossessionRentArrears;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.GroundsForPossession;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.MediationAndSettlement;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.NoRentArrearsGroundsForPossessionOptions;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.NoRentArrearsGroundsForPossessionReason;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.NoticeDetails;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.PreActionProtocol;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.RentArrears;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.RentDetails;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.ResumeClaim;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.SelectClaimType;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.SelectClaimantType;
import uk.gov.hmcts.reform.pcs.ccd.page.resumepossessionclaim.TenancyLicenceDetails;
import uk.gov.hmcts.reform.pcs.ccd.service.ClaimGroundService;
import uk.gov.hmcts.reform.pcs.ccd.service.ClaimService;
import uk.gov.hmcts.reform.pcs.ccd.service.PartyService;
import uk.gov.hmcts.reform.pcs.ccd.service.PcsCaseService;
import uk.gov.hmcts.reform.pcs.ccd.service.UnsubmittedCaseDataService;
import uk.gov.hmcts.reform.pcs.ccd.type.DynamicStringList;
import uk.gov.hmcts.reform.pcs.ccd.type.DynamicStringListElement;
import uk.gov.hmcts.reform.pcs.postcodecourt.model.LegislativeCountry;
import uk.gov.hmcts.reform.pcs.security.SecurityContextService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static feign.Util.isNotBlank;
import static uk.gov.hmcts.reform.pcs.ccd.domain.State.AWAITING_FURTHER_CLAIM_DETAILS;
import static uk.gov.hmcts.reform.pcs.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pcs.ccd.event.EventId.multiPartyTest;


@Slf4j
@Component
@AllArgsConstructor
public class MultiPartyTestEvent implements CCDConfig<PCSCase, State, UserRole> {

    @Override
    public void configure(ConfigBuilder<PCSCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(multiPartyTest.name(), this::submit, this::start)
            .forAllStates()
            .name("Multi party test")
            .grant(Permission.CRUD, UserRole.PCS_SOLICITOR)
            .showSummary()
            .fields()
            .page("page-1")
            .optional(PCSCase::getPartyToReplace);
//            .optional(PCSCase::getMyPartyList2)
//            .optional(PCSCase::getParty1);
//            .optional(PCSCase::getMyPartyList_0);
//            .optional(PCSCase::getParty2);
//            .optional(PCSCase::getMyMessage)
//            .mandatory(PCSCase::getMyPartyList);
    }

    private PCSCase start(EventPayload<PCSCase, State> eventPayload) {
        PCSCase caseData = eventPayload.caseData();

//        List<ListValue<Party>> myPartyList = Stream.of("P1", "P2", "P3", "P4")
//            .map(forname -> Party.builder().forename(forname).build())
//            .map(party -> new ListValue<>(party.getForename(), party))
//            .toList();

        List<Party> myPartyList = Stream.of("P1b")
            .map(forname -> Party.builder()
                .forename(forname)
                .surname("Surname for " + forname)
                .build())
            .toList();

//        caseData.setMyPartyList(myPartyList);
//        caseData.setMyMessage("Test message here");

//        caseData.setParty1(Party.builder().forename("party1").build());
//        caseData.setParty2(Party.builder().forename("party2").build());

        return caseData;
    }

    private void submit(EventPayload<PCSCase, State> eventPayload) {
        PCSCase caseData = eventPayload.caseData();

        System.out.println(caseData);
    }
}
