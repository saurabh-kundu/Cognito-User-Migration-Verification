package com.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class CommonHelper {

    @Autowired
    private CognitoModel cognitoModel;

     List<ListUsersResult> getAllUsersFromAWSAccount(AWSCognitoIdentityProvider awsCognitoIdentityProvider) {
        List<ListUsersResult> usersResults = new ArrayList<>();
        ListUsersResult listUsersResult;
        do {
            listUsersResult = awsCognitoIdentityProvider.listUsers(new ListUsersRequest()
                    .withUserPoolId(cognitoModel.getFromCognitoPoolId())
                    .withLimit(50));
            usersResults.add(listUsersResult);
        }while(listUsersResult.getPaginationToken() != null);
        return usersResults;
    }
}
