package com.doubledice.databuilder.bean;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.exceptions.OAuthException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.GroupAuthResponse;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author ponomarev 16.08.2022
 */
@Lazy
//@Data
@Component
public class BeanBuilder {
    public static final String CLIENT_ID = "spring.security.oauth2.client.registration.vk-app.clientId";
    public static final String CLIENT_SECRET = "spring.security.oauth2.client.registration.vk-app.clientSecret";
    public static final String REDIRECT_URI = "spring.security.oauth2.client.registration.vk-app.redirect-uri";
    public static final String GROUP_IDS = "spring.security.oauth2.client.registration.vk-app.group_ids";
    private static final String SERVICE_SECRET = "spring.security.oauth2.client.registration.vk-app.serviceSecret";

    private static String code;

    public void setCode(String code) {
        this.code = code;
    }

    @Autowired @Lazy
    private Environment env;
    @Autowired @Lazy
    private VkApiClient vk;
    @Autowired @Lazy
    private GroupAuthResponse groupAuthResponse;
    @Autowired @Lazy
    private UserAuthResponse userAuthResponse;
    @Autowired @Lazy
    private GroupActor groupActor;
    @Autowired @Lazy
    private UserActor userActor;
    @Autowired @Lazy
    private ServiceActor serviceActor;

    public VkApiClient getVk() {
        return vk;
    }

    public GroupAuthResponse getGroupAuthResponse() {
        return groupAuthResponse;
    }

    public UserAuthResponse getUserAuthResponse() {
        return userAuthResponse;
    }

    public GroupActor getGroupActor() {
        return groupActor;
    }

    public UserActor getUserActor() {
        return userActor;
    }

    public ServiceActor getServiceActor() {
        return serviceActor;
    }

    @Bean
    @Scope("singleton")
    public VkApiClient vkApiClient() {
        TransportClient transportClient = new HttpTransportClient();
        return new VkApiClient(transportClient);
    }

    /**
     *  clientSecret - защищенный ключ
     *
     * @return
     * @throws ApiException
     * @throws ClientException
     */
    @Lazy
    @Bean
    @Scope("singleton")
    public GroupAuthResponse groupAuthResponse() throws ApiException, ClientException {
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
        }catch (Exception e) {
             e.printStackTrace();
        }
        return authResp;
    }
    @Lazy
    @Bean
    @Scope("prototype")
//    @Scope("singleton")
    public GroupActor groupActor() {
//        GroupAuthResponse authResp = authResponse;
        Integer groupId = Integer.valueOf(Objects.requireNonNull(env.getProperty(GROUP_IDS)));
//        try {
//            authResp = vk.oAuth()
//                    .groupAuthorizationCodeFlow(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
//                            env.getProperty(CLIENT_SECRET),
//                            env.getProperty(REDIRECT_URI),
//                            VKLoginController.vkCode)
//                    .execute();
//        } catch (OAuthException e) {
//            e.getRedirectUri();
//        } catch (ClientException | ApiException e) {
//            throw new RuntimeException(e);
//        }
        return new GroupActor(groupId, groupAuthResponse.getAccessTokens().get(groupId));
    }


    @Lazy
    @Bean
//    @Scope("singleton")
    public UserAuthResponse userAuthResponse() throws ApiException, ClientException {
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return authResp;
    }

//        @Lazy
    @Bean
    @Scope("prototype")
//    @Scope("singleton")
    public UserActor userActor() {
        return new UserActor(userAuthResponse.getUserId(), userAuthResponse.getAccessToken());
    }

//    @Lazy
    @Bean
    @Scope("singleton")
    public ServiceActor serviceActor() {
        ServiceClientCredentialsFlowResponse authResp = null;
        try {
            authResp = vk.oAuth()
                    .serviceClientCredentialsFlow(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
                            env.getProperty(CLIENT_SECRET))
                    .execute();
        } catch (OAuthException e) {
            e.getRedirectUri();
        } catch (ClientException | ApiException e) {
            throw new RuntimeException(e);
        }
//        return new ServiceActor(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
//                Objects.requireNonNull(authResp).getAccessToken());
        return new ServiceActor(Integer.valueOf(Objects.requireNonNull(env.getProperty(CLIENT_ID))),
                env.getProperty(CLIENT_SECRET), env.getProperty(SERVICE_SECRET));
    }
}
