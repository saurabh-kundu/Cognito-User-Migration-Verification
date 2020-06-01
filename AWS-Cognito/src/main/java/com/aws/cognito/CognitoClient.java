package com.aws.cognito;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CognitoClient {

    @Autowired
    private CognitoModel cognitoModel;

    public AWSCognitoIdentityProvider getCognitoClient(AccountType accountType){
        BasicAWSCredentials basicAWSCredentials;
        String region;
        if(accountType.toString().equalsIgnoreCase("old")){
            basicAWSCredentials = new BasicAWSCredentials(cognitoModel.getFromAWSAccessKeyId(),
                    cognitoModel.getFromAWSSecretKeyId());
            region = cognitoModel.getFromAWSRegion();
        }else {
            basicAWSCredentials = new BasicAWSCredentials(cognitoModel.getToAWSAccessKeyId(),
                    cognitoModel.getToAWSSecretKeyId());
            region = cognitoModel.getToAWSRegion();
        }
        return AWSCognitoIdentityProviderClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(region)
                .build();
    }
}
