package ua.com.papers.crawler.test;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.services.authors.IAuthorService;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Created by Максим on 10/2/2017.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BasePublicationHandler {

    protected IAuthorService authorService;

    public BasePublicationHandler(IAuthorService authorService) {
        this.authorService = authorService;
    }

    @NotNull
    protected final int findAuthorId(String initials, String lastName, String fullName)
            throws NoSuchEntityException, ValidationException, ServiceErrorException {
        val authorView = new AuthorView();

        authorView.setLast_name(lastName);
        authorView.setInitials(initials);
        authorView.setOriginal(fullName);

        val masterOpt = Optional.ofNullable(authorService.findByNameMaster(lastName, initials));
        val authorOpt = Optional.ofNullable(authorService.findByOriginal(authorView.getOriginal()));

        if (masterOpt.isPresent()) {
            if (!authorOpt.isPresent()) {
                authorView.setMaster_id(masterOpt.get().getId());
                authorService.createAuthor(authorView);
            }
            return masterOpt.get().getId();
        }

        val masterView = new AuthorMasterView();

        masterView.setLast_name(lastName);
        masterView.setInitials(initials);

        if (authorOpt.isPresent()) {
            val author = authorOpt.get();
            final int id;

            if (author.getMaster() == null) {
                id = authorService.createAuthorMaster(masterView);
            } else {
                id = author.getMaster().getId();
            }

            val newMaster = authorService.getAuthorMasterById(id);

            author.setMaster(newMaster);
            authorService.updateAuthor(author);
            return id;
        }

        val id = authorService.createAuthorMaster(masterView);

        authorView.setMaster_id(id);
        authorService.createAuthor(authorView);
        return id;
    }

}
