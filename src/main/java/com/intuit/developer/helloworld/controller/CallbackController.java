package com.intuit.developer.helloworld.controller;

import com.intuit.developer.helloworld.client.OAuth2PlatformClientFactory;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class CallbackController {
    
	@Autowired
	OAuth2PlatformClientFactory factory;

    private static final Logger logger = Logger.getLogger(CallbackController.class);

    @RequestMapping("/oauth2redirect")
    public String callBackFromOAuth(Model model, @RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {
        logger.info("inside oauth2redirect of sample"  );
        try {
	        String csrfToken = (String) session.getAttribute("csrfToken");
	        if (csrfToken.equals(state)) {
	            session.setAttribute("realmId", realmId);
	            session.setAttribute("auth_code", authCode);
	
	            OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
	            String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");
	            logger.info("inside oauth2redirect of sample -- redirectUri " + redirectUri  );
	            
	            BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);
				 
	            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
	            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

				model.addAttribute("response", "Connected!");
	            return "connected";
	        }
	        logger.info("csrf token mismatch " );
        } catch (OAuthException e) {
        	logger.error("Exception in callback handler ", e);
		} 
        return null;
    }
}
