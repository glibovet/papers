package ua.com.papers.services.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ua.com.papers.services.recommendations.IRecommendationsService;

@ShellComponent
public class RecommendationsShellCommands {

    @Autowired
    private IRecommendationsService recommendationsService;

    @ShellMethod("Execute generate recommendations process")
    public void execute() {
        // invoke service
        recommendationsService.generate();
    }

}
