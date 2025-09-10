package uk.gov.hmcts.reform.pcs.ccd.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.AddressUK;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefendantDetails {

    @CCD(label = "Defendant's first name")
    private String firstName;

    @CCD(label = "Car details")
    private CarType carType;

    @CCD(label = "Defendant's last name")
    private String lastName;

}

