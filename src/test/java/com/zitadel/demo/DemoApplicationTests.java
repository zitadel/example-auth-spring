package com.zitadel.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "server.port=3000",
    "SESSION_DURATION=3600",
    "SESSION_SECRET=test-secret-key-for-pytest",
    "ZITADEL_DOMAIN=https://test.us1.zitadel.cloud",
    "ZITADEL_CLIENT_ID=mock-client-id",
    "ZITADEL_CLIENT_SECRET=mock-client-secret",
    "ZITADEL_CALLBACK_URL=http://localhost:3000/auth/callback",
    "ZITADEL_POST_LOGOUT_URL=http://localhost:3000/auth/logout/callback"
})
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    void testSigninPageLoads() throws Exception {
        mockMvc.perform(get("/auth/signin"))
            .andExpect(status().isOk());
    }

    @Test
    void testProfileRedirectsWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testCsrfEndpointWorks() throws Exception {
        mockMvc.perform(get("/auth/csrf"))
            .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(
                ClientRegistration.withRegistrationId("zitadel")
                    .clientId("mock-client-id")
                    .clientSecret("mock-client-secret")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/auth/callback")
                    .authorizationUri("https://test.us1.zitadel.cloud/oauth/v2/authorize")
                    .tokenUri("https://test.us1.zitadel.cloud/oauth/v2/token")
                    .userInfoUri("https://test.us1.zitadel.cloud/oidc/v1/userinfo")
                    .jwkSetUri("https://test.us1.zitadel.cloud/oauth/v2/keys")
                    .build()
            );
        }
    }

}
