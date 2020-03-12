package mops.module;

import static mops.module.keycloak.KeycloakMopsAccount.createAccountFromPrincipal;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;


@Controller
@SessionScope
@RequestMapping("/module")
public class AdministratorController {

//    TODO waiting for Christian Meter to create a new role for admins for us
//    @GetMapping("/administrator")
//    @Secured("ROLE_orga")
//    public String administrator(KeycloakAuthenticationToken token, Model model) {
//        model.addAttribute("account", createAccountFromPrincipal(token));
//        return "administrator";
//    }

}