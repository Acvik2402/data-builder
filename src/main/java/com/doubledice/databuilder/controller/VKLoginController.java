package com.doubledice.databuilder.controller;

import com.doubledice.databuilder.bean.BeanBuilder;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author ponomarev 14.08.2022
 */
@Controller()
//@AllArgsConstructor
@RequestMapping("/vk")
public class VKLoginController {
    @Autowired @Lazy
    private BeanBuilder beanBuilder;


//    public static String vkCode;

    @Autowired
    private Environment env;

    //redirect to external URL
    @RequestMapping(value = "/authorize")
    public String VKAutorize(Model model, RedirectAttributes attr) {
        attr.addAttribute("client_id", env.getProperty("spring.security.oauth2.client.registration.vk-app.clientId")); // - обязательный — идентификатор вашего приложения
        attr.addAttribute("redirect_uri", env.getProperty("spring.security.oauth2.client.registration.vk-app.redirect-uri")); // - обязательный — адрес, на который будет передан code (домен указанного адреса должен соответствовать основному домену в настройках приложения и перечисленным значениям в списке доверенных redirect uri — адреса сравниваются вплоть до path-части).
//        attr.addAttribute("group_ids", env.getProperty("spring.security.oauth2.client.registration.vk-app.group_ids")); // -обязательный — идентификаторы сообществ, для которых необходимо получить ключ доступа. Параметр должен представлять собой строку, содержащую значения без знака «минус», разделенные запятой.
//        attr.addAttribute("scope", "friends, groups"); // — битовая маска настроек доступа приложения, которые необходимо проверить при авторизации и запросить отсутствующие.
//        attr.addAttribute("display", "popup"); // — Указывает тип отображения страницы авторизации..
//        attr.addAttribute("response_type", "code"); // - Тип ответа, который Вы хотите получить. Укажите code.
//        attr.addAttribute("v", "5.131"); // - обязательный — версия API, которую вы используете. Актуальная версия: 5.131.
        return "redirect:https://oauth.vk.com/authorize";
    }

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String settingCode(@RequestParam(name = "code") String code) {
        if (StringUtils.isNotEmpty(code)) {
            try {
                beanBuilder.setCode(code);
                beanBuilder.serviceActor();
//                beanBuilder.userAuthResponse();
//                beanBuilder.groupAuthResponse();
                return "redirect:/group/groups";
//            } catch (ApiException | ClientException ex) {
//                exception.printStackTrace();
//                return "redirect:/vk/authorize";
            }catch (Exception exception) {
                exception.printStackTrace();
//                return "redirect:/vk/authorize";
            }
        }
        return "redirect:/vk/authorize";
    }
}
