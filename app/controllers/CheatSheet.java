package controllers;

import java.util.List;
import java.util.Map;

import models.Author;
import models.Repo;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.modules.twig.Twig;
import client.GithubClient;
import client.GithubException;

public class CheatSheet extends Application {

    public static void list() {
        Map<String, Repo> storedRepos = Repo.findAllWithKeys();
        render(storedRepos);
    }

    public static void show(String authorName, String githubUser, String repoName) {
        Repo repo = Repo.findByAuthorNameAndRepo(authorName, githubUser, repoName);

        if (repo == null) {
            list();
        }

        render(repo);
    }

    @Secure
    public static void create() {
        render();
    }

    @Secure
    public static void save(
            @Required String title,
            @Required String githubUser,
            @Required String repoName) {

        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            create();
        }

        try {
            List<String> sheets = new GithubClient(githubUser, repoName).getSheets();

            Repo repo = Repo.findByAuthorEmailAndRepo(GAE.getUser().getEmail(), githubUser, repoName);

            if (repo == null) {
                repo = new Repo();
                repo.author = Author.findByEmail(GAE.getUser().getEmail());
                repo.githubUser = githubUser;
                repo.repoName = repoName;
                repo.store();
            }

            repo.title = title;
            repo.sheets = sheets;
            repo.update();

            list();
        } catch (GithubException e) {
            flash.put("githubError", e.getMessage());
            params.flash();
            create();
        }
    }

    @Secure
    public static void delete(String authorName, String githubUser, String repoName) {
        Repo repo = Repo.findByAuthorNameAndRepo(authorName, githubUser, repoName);

        if (repo != null) {
            if (!repo.author.email.equals(GAE.getUser().getEmail())) {
                forbidden();
            }
            Twig.delete(repo);
        }

        list();
    }
}
