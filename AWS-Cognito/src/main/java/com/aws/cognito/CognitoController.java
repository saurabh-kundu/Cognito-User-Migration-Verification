package com.aws.cognito;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
@Controller
@Slf4j
public class CognitoController {

    @Autowired
    private MigrationHelper migrationHelper;
    @Autowired
    private VerificationHelper verificationHelper;

    @PostMapping("/migration")
    public void doMigration(@RequestBody CognitoModel cognitoModel) {
        log.info("Executing cognito users migration...");
        migrationHelper.migrateUsers(cognitoModel);
    }

    @PostMapping("/migration")
    public void doVerification(@RequestBody CognitoModel cognitoModel) {
        log.info("Executing cognito users verification...");
        verificationHelper.verifyUsers(cognitoModel);
    }
}
