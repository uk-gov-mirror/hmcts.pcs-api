import {test} from '@playwright/test';
import {initializeExecutor, performAction, performValidation, performValidations} from '@utils/controller';
import {addressDetails} from '@data/page-data/addressDetails.page.data';
import {claimantType} from '@data/page-data/claimantType.page.data';
import {claimType} from '@data/page-data/claimType.page.data';
import {claimantName} from '@data/page-data/claimantName.page.data';
import {contactPreferences} from '@data/page-data/contactPreferences.page.data';
import {defendantDetails} from '@data/page-data/defendantDetails.page.data';
import {tenancyLicenceDetails} from '@data/page-data/tenancyLicenceDetails.page.data';
import {groundsForPossession} from '@data/page-data/groundsForPossession.page.data';
import {rentArrearsPossessionGrounds} from '@data/page-data/rentArrearsPossessionGrounds.page.data';
import {preActionProtocol} from '@data/page-data/preActionProtocol.page.data';
import {mediationAndSettlement} from '@data/page-data/mediationAndSettlement.page.data';
import {noticeOfYourIntention} from '@data/page-data/noticeOfYourIntention.page.data';
import {noticeDetails} from '@data/page-data/noticeDetails.page.data';
import {rentDetails} from '@data/page-data/rentDetails.page.data';
import {dailyRentAmount} from '@data/page-data/dailyRentAmount.page.data';
import {provideMoreDetailsOfClaim} from '@data/page-data/provideMoreDetailsOfClaim.page.data';
import {whatAreYourGroundsForPossession} from '@data/page-data/whatAreYourGroundsForPossession.page.data';
import {reasonsForPossession} from '@data/page-data/reasonsForPossession.page.data';
import {moneyJudgment} from '@data/page-data/moneyJudgment.page.data';
import {claimantCircumstances} from '@data/page-data/claimantCircumstances.page.data';
import {applications} from '@data/page-data/applications.page.data';
import {completeYourClaim} from '@data/page-data/completeYourClaim.page.data';
import {user} from '@data/user-data/permanent.user.data';
import {reasonsForRequestingASuspensionOrder} from '@data/page-data/reasonsForRequestingASuspensionOrder.page.data';
import {checkYourAnswers} from '@data/page-data/checkYourAnswers.page.data';
import {propertyDetails} from '@data/page-data/propertyDetails.page.data';
import {languageUsed} from '@data/page-data/languageUsed.page.data';
import {defendantCircumstances} from '@data/page-data/defendantCircumstances.page.data';
import {claimingCosts} from '@data/page-data/claimingCosts.page.data';
import {uploadAdditionalDocs} from '@data/page-data/uploadAdditionalDocs.page.data';
import {statementOfTruth} from '@data/page-data/statementOfTruth.page.data';
import {home} from '@data/page-data/home.page.data';
import {additionalReasonsForPossession} from '@data/page-data/additionalReasonsForPossession.page.data';
import {underlesseeOrMortgageeEntitledToClaim} from '@data/page-data/underlesseeOrMortgageeEntitledToClaim.page.data';
import {alternativesToPossession} from '@data/page-data/alternativesToPossession.page.data';
import {housingAct} from '@data/page-data/housingAct.page.data';
import {reasonsForRequestingADemotionOrder} from '@data/page-data/reasonsForRequestingADemotionOrder.page.data';
import {statementOfExpressTerms} from '@data/page-data/statementOfExpressTerms.page.data';
import {wantToUploadDocuments} from '@data/page-data/wantToUploadDocuments.page.data';
import {reasonsForRequestingASuspensionAndDemotionOrder} from '@data/page-data/reasonsForRequestingASuspensionAndDemotionOrder.page.data';
import {caseNumber} from "@utils/actions/custom-actions/createCase.action";

test.beforeEach(async ({page}) => {
  initializeExecutor(page);
  await performAction('navigateToUrl', process.env.MANAGE_CASE_BASE_URL);
  await performAction('login', user.claimantSolicitor);
  await page.getByRole('button', { name: 'Accept analytics cookies' }).click();
  await performAction('clickTab', home.createCaseTab);
  await performAction('selectJurisdictionCaseTypeEvent');
  await performAction('housingPossessionClaim');
});

test.describe('[Create Case - England] @Master @nightly', async () => {
  test('England - Assured tenancy with Rent arrears and other possession grounds', async ({page}) => {
    await performAction('selectAddress', {
      postcode: addressDetails.englandCourtAssignedPostcode,
      addressIndex: addressDetails.addressIndex
    });
    await performValidation('bannerAlert', 'Case #.* has been created.');
    await performAction('extractCaseIdFromAlert');
    await performAction('clickButtonAndVerifyPageNavigation', provideMoreDetailsOfClaim.continue, claimantType.mainHeader);
    await page.waitForTimeout(10000); // wait 3 seconds
    await performAction('navigateToUrl', 'https://xui-pcs-api-pr-316.preview.platform.hmcts.net/cases/case-details/PCS/PCS-316/'+caseNumber.replaceAll('-', '')+'/trigger/resumePossessionClaim/resumePossessionClaimcontactPreferences');
    await page.waitForTimeout(10000); // wait 3 seconds
    await performValidation('mainHeader',contactPreferences.mainHeader)
    await performAction('navigateToUrl', 'https://xui-pcs-api-pr-316.preview.platform.hmcts.net/cases/case-details/PCS/PCS-316/'+caseNumber.replaceAll('-', '')+'/trigger/resumePossessionClaim/resumePossessionClaimdefendantsDetails');
    await page.waitForTimeout(10000); // wait 3 seconds
    await performValidation('mainHeader',defendantDetails.mainHeader)
    await performAction('navigateToUrl', 'https://xui-pcs-api-pr-316.preview.platform.hmcts.net/cases/case-details/PCS/PCS-316/'+caseNumber.replaceAll('-', '')+'/trigger/resumePossessionClaim/resumePossessionClaimpreActionProtocol');
    await page.waitForTimeout(10000); // wait 3 seconds
    await performValidation('mainHeader',preActionProtocol.mainHeader)
    await page.waitForTimeout(10000); // wait 3 seconds
    await performAction('navigateToUrl', 'https://xui-pcs-api-pr-316.preview.platform.hmcts.net/cases/case-details/PCS/PCS-316/'+caseNumber.replaceAll('-', '')+'/trigger/resumePossessionClaim/resumePossessionClaimgroundsForPossession');
    await page.waitForTimeout(10000); // wait 3 seconds
    await performValidation('mainHeader',groundsForPossession.mainHeader)
  });
});

