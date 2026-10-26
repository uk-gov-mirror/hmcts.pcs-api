package uk.gov.hmcts.reform.pcs.ccd.service.bulkprint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.reform.pcs.ccd.entity.AddressEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.ClaimEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.DocumentEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.PcsCaseEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyEntity;
import uk.gov.hmcts.reform.pcs.ccd.entity.party.PartyRole;
import uk.gov.hmcts.reform.pcs.ccd.repository.PcsCaseRepository;
import uk.gov.hmcts.reform.pcs.ccd.service.form.DefenceCorrespondenceAddressResolver;
import uk.gov.hmcts.reform.pcs.ccd.service.form.RecipientAddressResolver;
import uk.gov.hmcts.reform.pcs.ccd.util.AddressMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.gov.hmcts.reform.pcs.ccd.service.form.PartyDisplayMapper.partiesByRole;

/**
 * Resolves each pack recipient (name + postal address) in a read-only transaction, so the senders can post
 * outside any transaction and never hold a DB connection across the external render/fetch/merge/post calls.
 */
@Service
public class PackRecipientResolver {

    private final PcsCaseRepository pcsCaseRepository;
    private final ClaimPackSelector claimPackSelector;
    private final DefencePackSelector defencePackSelector;
    private final GenAppPackSelector genAppPackSelector;
    private final RecipientAddressResolver recipientAddressResolver;
    private final DefenceCorrespondenceAddressResolver defenceCorrespondenceAddressResolver;
    private final AddressMapper addressMapper;

    public PackRecipientResolver(PcsCaseRepository pcsCaseRepository,
                                 ClaimPackSelector claimPackSelector,
                                 DefencePackSelector defencePackSelector,
                                 GenAppPackSelector genAppPackSelector,
                                 RecipientAddressResolver recipientAddressResolver,
                                 DefenceCorrespondenceAddressResolver defenceCorrespondenceAddressResolver,
                                 AddressMapper addressMapper) {
        this.pcsCaseRepository = pcsCaseRepository;
        this.claimPackSelector = claimPackSelector;
        this.defencePackSelector = defencePackSelector;
        this.genAppPackSelector = genAppPackSelector;
        this.recipientAddressResolver = recipientAddressResolver;
        this.defenceCorrespondenceAddressResolver = defenceCorrespondenceAddressResolver;
        this.addressMapper = addressMapper;
    }

    @Transactional(readOnly = true)
    public List<ResolvedRecipient> resolveClaimRecipients(UUID caseId) {
        return pcsCaseRepository.findById(caseId)
            .map(pcsCase -> claimPackSelector.findClaimPackCandidates(pcsCase).stream()
                .map(candidate -> resolveClaimRecipient(pcsCase, candidate))
                .toList())
            .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<ResolvedRecipient> resolveDefenceRecipients(UUID caseId) {
        return pcsCaseRepository.findById(caseId)
            .map(pcsCase -> defencePackSelector.findDefencePackCandidates(pcsCase).stream()
                .map(candidate -> resolveDefenceRecipient(pcsCase, candidate))
                .toList())
            .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<ResolvedRecipient> resolveGenAppRecipients(UUID caseId) {
        return pcsCaseRepository.findById(caseId)
            .map(pcsCase -> genAppPackSelector.findGenAppPackCandidates(pcsCase).stream()
                .map(candidate -> resolveGenAppRecipient(pcsCase, candidate))
                .toList())
            .orElseGet(List::of);
    }

    private ResolvedRecipient resolveClaimRecipient(PcsCaseEntity pcsCase, ClaimPackCandidate candidate) {
        PartyEntity recipient = candidate.party();
        PartyRole role = candidate.recipientType();
        return new ResolvedRecipient(pcsCase, recipient, claimLetterType(role), candidate.documents(),
            recipientAddressResolver.resolveDisplayName(recipient),
            postalAddress(recipient, role, pcsCase.getPropertyAddress()), null);
    }

    private ResolvedRecipient resolveDefenceRecipient(PcsCaseEntity pcsCase, DefencePackCandidate candidate) {
        PartyEntity recipient = candidate.recipient();
        PartyRole role = candidate.role();
        List<DocumentEntity> documents = candidate.documents();
        return new ResolvedRecipient(pcsCase, recipient, defenceLetterType(documents), documents,
            recipientAddressResolver.resolveDisplayName(recipient),
            correspondenceAddress(recipient, role, pcsCase.getPropertyAddress()), null);
    }

    // DEF-01-IN1 when any form comes from a legal representative's response.
    private LetterType defenceLetterType(List<DocumentEntity> documents) {
        return documents.stream().anyMatch(LegalRepResponseDocuments::isFromLegalRepResponse)
            ? LetterType.DEFENCE_PACK_LEGAL_REP
            : LetterType.DEFENCE_PACK;
    }

    private ResolvedRecipient resolveGenAppRecipient(PcsCaseEntity pcsCase, GenAppPackCandidate candidate) {
        PartyEntity recipient = candidate.recipient();
        PartyRole role = candidate.role();
        return new ResolvedRecipient(pcsCase, recipient, LetterType.GEN_APP_PACK, candidate.documents(),
            recipientAddressResolver.resolveDisplayName(recipient),
            correspondenceAddress(recipient, role, pcsCase.getPropertyAddress()), caseName(pcsCase));
    }

    // "Claimant v Defendant" from the lead party in each role, resolved in-transaction for the cover letter.
    private String caseName(PcsCaseEntity pcsCase) {
        if (pcsCase.getClaims().isEmpty()) {
            return null;
        }
        ClaimEntity claim = pcsCase.getClaims().getFirst();
        String claimant = leadPartyName(partiesByRole(claim, PartyRole.CLAIMANT));
        String defendant = leadPartyName(partiesByRole(claim, PartyRole.DEFENDANT));
        if (claimant == null && defendant == null) {
            return null;
        }
        return StringUtils.defaultString(claimant) + " v " + StringUtils.defaultString(defendant);
    }

    private String leadPartyName(List<PartyEntity> parties) {
        return parties.stream()
            .filter(party -> !party.isRemoved())
            .map(this::partyName)
            .filter(StringUtils::isNotBlank)
            .findFirst()
            .orElse(null);
    }

    private String partyName(PartyEntity party) {
        if (StringUtils.isNotBlank(party.getOrgName())) {
            return party.getOrgName();
        }
        return Stream.of(party.getFirstName(), party.getLastName())
            .filter(StringUtils::isNotBlank)
            .reduce((a, b) -> a + " " + b)
            .orElse(null);
    }

    private AddressUK correspondenceAddress(PartyEntity recipient, PartyRole role, AddressEntity propertyAddress) {
        if (role == PartyRole.DEFENDANT) {
            return defenceCorrespondenceAddressResolver.resolveCorrespondenceAddress(recipient, propertyAddress);
        }
        return postalAddress(recipient, role, propertyAddress);
    }

    private AddressUK postalAddress(PartyEntity recipient, PartyRole role, AddressEntity propertyAddress) {
        AddressEntity resolved = recipientAddressResolver.resolvePostalAddress(recipient, role, propertyAddress);
        return resolved == null ? null : addressMapper.toAddressUK(resolved);
    }

    private LetterType claimLetterType(PartyRole role) {
        return role == PartyRole.CLAIMANT ? LetterType.CLAIMANT_CLAIM_PACK : LetterType.DEFENDANT_CLAIM_PACK;
    }
}
