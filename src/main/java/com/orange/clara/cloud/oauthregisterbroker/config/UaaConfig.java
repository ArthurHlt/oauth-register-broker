package com.orange.clara.cloud.oauthregisterbroker.config;

import com.orange.clara.cloud.oauthregisterbroker.cloudfoundry.CloudFoundryClientFactory;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.net.MalformedURLException;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 16/05/2016
 */
@Configuration
@Profile(value = "uaa")
public class UaaConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(UaaConfig.class);
    @Autowired
    private CloudFoundryClientFactory cloudFoundryClientFactory;

    @Value("${cloud.controller.url}")
    private String cloudControllerUrl;


    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OAuth2AccessToken getOAuth2AccessToken() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        return new DefaultOAuth2AccessToken(details.getTokenValue());
    }

    @Bean(name = "cloudFoundryClientAsUser")
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CloudFoundryClient getCloudFoundryClientAsUser() throws MalformedURLException {
        LOGGER.debug("Creating new CloudFoundry client using access token");
        return cloudFoundryClientFactory.createCloudFoundryClient(getOAuth2AccessToken(), this.cloudControllerUrl);
    }

}

