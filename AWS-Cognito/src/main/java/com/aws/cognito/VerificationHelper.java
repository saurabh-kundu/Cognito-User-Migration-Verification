package com.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
class VerificationHelper {

    @Autowired
    private CognitoClient cognitoClient;
    @Autowired
    private MigrationHelper migrationHelper;
    @Autowired
    private CommonHelper commonHelper;

    void verifyUsers(CognitoModel cognitoModel) {
        List<String> errorList = new ArrayList<>();
        AWSCognitoIdentityProvider fromAwsCognitoIdentityProvider = cognitoClient
                .getCognitoClient(AccountType.OLD);
        AWSCognitoIdentityProvider toAwsCognitoIdentityProvider = cognitoClient
                .getCognitoClient(AccountType.NEW);
        List<UserType> fromUserTypes = commonHelper.getAllUsersFromAWSAccount(fromAwsCognitoIdentityProvider).stream()
                .flatMap(listUsersResult -> listUsersResult.getUsers().stream()).collect(Collectors.toList());
        List<UserType> toUserTypes = commonHelper.getAllUsersFromAWSAccount(toAwsCognitoIdentityProvider).stream()
                .flatMap(listUsersResult -> listUsersResult.getUsers().stream()).collect(Collectors.toList());
        doVerification(fromUserTypes, toUserTypes, errorList);
        if(errorList.isEmpty())
            log.info("All users all attributed match...");
        else
            errorList.forEach(error -> log.error("Following errors occurred : {} ", error));
    }

    private void doVerification(List<UserType> fromUserTypes, List<UserType> toUserTypes, List<String> errorList) {
        Map<String, UserType> toUserTypeMap = getToUserTypesMap(toUserTypes);
        fromUserTypes.forEach(fromUserType -> {
            UserType toUserType = toUserTypeMap.get(fromUserType.getUsername());
            if(Objects.nonNull(toUserType)) {
                List<String> attributeErrorList = new ArrayList<>();
                matchAttributes(fromUserType.getAttributes(), toUserType.getAttributes(), attributeErrorList);
                if(!attributeErrorList.isEmpty())
                    attributeErrorList.forEach(attributeError -> errorList.add("The following userName : " +
                            fromUserType.getUsername() + "does not match this attribute : " + attributeError));
            } else
                errorList.add("The following username does not exist in toMigrated AWS account : "
                        +fromUserType.getUsername());
        });
    }

    private void matchAttributes(List<AttributeType> fromAttributes, List<AttributeType> toAttributes,
                                 List<String> attributeErrorList) {
        Map<String, String> toAttributeTypeMap = toAttributesMap(toAttributes);
        fromAttributes.forEach(fromAttributeType -> {
            String toAttributeValue = toAttributeTypeMap.get(fromAttributeType.getName());
            if(Objects.nonNull(toAttributeValue)) {
                if (!fromAttributeType.getValue().equals(toAttributeValue))
                    attributeErrorList.add(fromAttributeType.getName());
            } else
                attributeErrorList.add(fromAttributeType.getName());
        });
    }


    private Map<String, UserType> getToUserTypesMap(List<UserType> toUserTypes) {
        Map<String, UserType> userTypeMap = new HashMap<>();
        toUserTypes.forEach(userType -> userTypeMap.put(userType.getUsername(), userType));
        return userTypeMap;
    }

    private Map<String, String> toAttributesMap(List<AttributeType> attributeTypes) {
        Map<String, String> attributeTypeMap = new HashMap<>();
        attributeTypes.forEach(attributeType -> attributeTypeMap.put(attributeType.getName(),
                attributeType.getValue()));
        return attributeTypeMap;
    }
}
