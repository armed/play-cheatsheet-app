package controllers;

import models.Author;
import play.modules.gae.GAE;
import play.mvc.Before;
import play.mvc.Controller;

import com.google.appengine.api.users.User;

public class Application extends Controller {

    @Before(unless = { "login", "logout" })
    static void userParams() {
        if (GAE.isLoggedIn()) {
            User user = GAE.getUser();
            renderArgs.put("user", user);
        }
    }

    @Before
    static void checkSecurity() {
        if (!GAE.isLoggedIn()) {
            Secure secure = getActionAnnotation(Secure.class);
            if (secure != null) {
                forbidden();
            }
        }
    }

    @Before(only = { "CheatSheet.create" })
    static void checkIsAuthor() {
        if (GAE.isLoggedIn()) {
            Author a = Author.findByEmail(GAE.getUser().getEmail());

            if (a == null) {
                Registration.index();
            }
        }
    }

    public static void logout() {
        GAE.logout("CheatSheet.list");
    }

    public static void login() {
        GAE.login("CheatSheet.list");
    }
}