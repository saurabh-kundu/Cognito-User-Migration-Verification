package com.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
class MigrationHelper {

    @Autowired
    private CognitoClient cognitoClient;
    @Autowired
    private CognitoModel cognitoModel;
    @Autowired
    private CommonHelper commonHelper;

    void migrateUsers(CognitoModel cognitoModel) {
        AWSCognitoIdentityProvider fromAwsCognitoIdentityProvider = cognitoClient
                .getCognitoClient(AccountType.OLD);
        AWSCognitoIdentityProvider toAwsCognitoIdentityProvider = cognitoClient
                .getCognitoClient(AccountType.NEW);
        List<UserType> fromUserTypes = commonHelper.getAllUsersFromAWSAccount(fromAwsCognitoIdentityProvider).stream()
                .flatMap(listUsersResult -> listUsersResult.getUsers().stream()).collect(Collectors.toList());
        log.info("Total users from AWS account : {}", fromUserTypes.size());

        List<AdminCreateUserResult> adminCreateUserResultList = addUsersToToAwsAccountUserPool(
                fromUserTypes, toAwsCognitoIdentityProvider);
        log.info("Total users added : {}", adminCreateUserResultList.size());
    }

    private List<AdminCreateUserResult> addUsersToToAwsAccountUserPool(List<UserType> userTypes,
                                                AWSCognitoIdentityProvider toAwsCognitoIdentityProvider) {
        String userPoolId = cognitoModel.getToCognitoPoolId();
        List<AdminCreateUserResult> adminCreateUserResultList = new ArrayList<>();
        userTypes.forEach(userType -> {
            adminCreateUserResultList.add(toAwsCognitoIdentityProvider.adminCreateUser(new AdminCreateUserRequest()
            .withUserPoolId(userPoolId)
            .withUsername(userType.getUsername())
            .withUserAttributes(userType.getAttributes())));
        });
        return adminCreateUserResultList;
    }
}
