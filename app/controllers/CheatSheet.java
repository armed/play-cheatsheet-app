package controllers;

import java.util.List;
import java.util.Map;

import models.Author;
import models.Repo;
import models.Sheet;
import play.data.validation.Required;
import play.modules.gae.GAE;
import client.GithubClient;
import client.GithubException;

public class CheatSheet extends Application {

    public static void list() {
        Map<String, Repo> storedRepos = Repo.findAll();
        render(storedRepos);
    }

    public static void show(Long id) {
        Repo repo = Repo.findById(id);
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
            List<Sheet> sheets = new GithubClient(githubUser, repoName).getSheets();

            Repo repo = Repo.findByAuthorAndRepo(GAE.getUser().getEmail(), githubUser, repoName);

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
}
