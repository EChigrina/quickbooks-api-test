package com.intuit.developer.helloworld.controller;

import com.intuit.developer.helloworld.client.OAuth2PlatformClientFactory;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.exception.InvalidRequestException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
	
	private static final Logger logger = Logger.getLogger(HomeController.class);
	
	@Autowired
	OAuth2PlatformClientFactory factory;
	    
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/connected")
	public String connected(@ModelAttribute("response") String response, Model model) {
		model.addAttribute("response", response);
		return "connected";
	}

	/*@RequestMapping(value="/test", method = RequestMethod.GET)
	public ResponseEntity<String> test() {
		final String uri = "https://dxfeed-quickbooksintegration.cs17.force.com/services/apexrest/SalesforceQuickbooksIntegration";

		Map<String, String> params = new HashMap<>();
		params.put("payload", "3");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<Object>(params, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.postForEntity( uri, entity, String.class);

		System.out.println(result.getStatusCode());

		return new ResponseEntity<String>(result.getBody(), HttpStatus.OK);
	}*/

	@RequestMapping("/connectToQuickbooks")
	public View connectToQuickbooks(HttpSession session) {
		logger.info("inside connectToQuickbooks ");
		OAuth2Config oauth2Config = factory.getOAuth2Config();
		
		String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri"); 
		
		String csrf = oauth2Config.generateCSRFToken();
		session.setAttribute("csrfToken", csrf);
		try {
			List<Scope> scopes = new ArrayList<>();
			scopes.add(Scope.Accounting);
			return new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true, false);
		} catch (InvalidRequestException e) {
			logger.error("Exception calling connectToQuickbooks ", e);
		}
		return null;
	}

}
