package com.aws.cognito;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CognitoModel {

    private String fromCognitoPoolId;
    private String fromAWSAccessKeyId;
    private String fromAWSSecretKeyId;
    private String fromAWSRegion;

    private String toCognitoPoolId;
    private String toAWSAccessKeyId;
    private String toAWSSecretKeyId;
    private String toAWSRegion;
}
