package com.finsoft.g2pg.test.testplayerportal.controller;

import com.finsoft.g2pg.test.testplayerportal.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Value("${app.player-portal.url}")
    private String playerPortalUrl;
    @Value("${app.player-portal.domain-name}")
    private String domainName;
    @Value("${app.player-portal.cashier-name}")
    private String cashierName;
    @Value("${app.player-portal.profile-id}")
    private String profileId;

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request, HttpSession session) {
        User user = User.getFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        String portalUrl = loadPlayerPortalUrl(session);
        model.addAttribute("playerPortalUrl", portalUrl);

        log.info("playerPortalUrl: {}", portalUrl);
        return "home";
    }

    private String loadPlayerPortalUrl(HttpSession session) {
        String baseUrl = playerPortalUrl;
        URI baseUri;
        try {
            baseUri = URI.create(baseUrl);
        } catch (Exception ex) {
            log.error("Error parsing config URL: {}", baseUrl, ex);
            return "";
        }
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(baseUri)
                .queryParam("tempSessionId", getTempSessId(session))
                .queryParam("languageId", getLanguageId(session))
                .queryParam("domainName", domainName)
                .queryParam("cashierName", cashierName)
                .queryParam("profileId", profileId)
                ;

        return builder.toUriString();
    }

    private String getLanguageId(HttpSession session) {
        if (session != null) {
            I18NController controller = (I18NController) session.getAttribute("controller");
            if (controller != null && controller.getLanguageId() != null) {
                return controller.getLanguageId();
            }
        }
        return "en";
    }

    private String getTempSessId(HttpSession session) {
        return (String) session.getAttribute("tempSessId");
    }

}

