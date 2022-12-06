package com.doubledice.databuilder.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.GroupAuthResponse;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * @author ponomarev 16.08.2022
 */
@Lazy
@Configuration
public class BeansVKConfig {
    public static final String CLIENT_ID = "spring.security.oauth2.client.registration.vk-app.clientId";
    public static final String CLIENT_SECRET = "spring.security.oauth2.client.registration.vk-app.clientSecret";
    public static final String REDIRECT_URI = "spring.security.oauth2.client.registration.vk-app.redirect-uri";
    public static final String GROUP_IDS = "spring.security.oauth2.client.registration.vk-app.group_ids";
    private static final String SERVICE_SECRET = "spring.security.oauth2.client.registration.vk-app.serviceSecret";

    private static String code;

    public void setCode(String code) {
        this.code = code;
    }

    @Autowired
    @Lazy
    private Environment env;


    @Bean
    @Scope("singleton")
    public VkApiClient vkApiClient() {
        TransportClient transportClient = new HttpTransportClient();
        return new VkApiClient(transportClient);
    }

    /**
     * clientSecret - защищенный ключ
     *
     * @return
     * @throws ApiException
     * @throws ClientException
     */
    @Lazy
    @Bean
    @Scope("singleton")
    public GroupAuthResponse groupAuthResponse(VkApiClient vk) throws ApiException, ClientException {
        GroupAuthResponse authResp = null;
        try {
            authResp = vk.oAuth()
                    .groupAuthorizationCodeFlow(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
                            env.getProperty(CLIENT_SECRET),
                            env.getProperty(REDIRECT_URI),
                            code)
                    .execute();
        } catch (ApiException | ClientException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authResp;
    }

    @Lazy
    @Bean
    @Scope("prototype")
    public GroupActor groupActor(GroupAuthResponse groupAuthResponse) {
        Integer groupId = Integer.valueOf(Objects.requireNonNull(env.getProperty(GROUP_IDS)));
        return new GroupActor(groupId, groupAuthResponse.getAccessTokens().get(groupId));
    }



//  @Bean(initMethod = "") maybe can use
    @Lazy
    @Bean
    public UserAuthResponse userAuthResponse(VkApiClient vk) throws ApiException, ClientException {
        UserAuthResponse authResp = null;
        try {
            authResp = vk.oAuth()
                    .userAuthorizationCodeFlow(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
                            env.getProperty(CLIENT_SECRET),
                            env.getProperty(REDIRECT_URI),
                            code)
                    .execute();
        } catch (ApiException | ClientException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authResp;
    }


    @Bean
    @Scope("prototype")
    public UserActor userActor(UserAuthResponse userAuthResponse) {
        return new UserActor(userAuthResponse.getUserId(), userAuthResponse.getAccessToken());
    }

    @Bean
    @Scope("singleton")
    public ServiceActor serviceActor() {
        ServiceClientCredentialsFlowResponse authResp = null;
        return new ServiceActor(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
                env.getProperty(CLIENT_SECRET), env.getProperty(SERVICE_SECRET));
    }
}
