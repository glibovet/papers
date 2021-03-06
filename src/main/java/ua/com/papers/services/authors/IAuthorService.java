package ua.com.papers.services.authors;

import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Andrii on 28.09.2016.
 */
public interface IAuthorService {

    AuthorEntity getAuthorById(int id) throws NoSuchEntityException;
    List<AuthorEntity> getAuthors(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    AuthorMasterEntity getAuthorMasterById(int id) throws NoSuchEntityException;
    List<AuthorMasterEntity> getAuthorMasters(int offset, int limit, String restrict) throws NoSuchEntityException, WrongRestrictionException;

    Map<String, Object> getAuthorMapById(int id, Set<String> fields) throws NoSuchEntityException;

    Map<String, Object> getAuthorMasterMapId(int id, Set<String> fields) throws NoSuchEntityException;

    List<Map<String,Object>> getAuthorsMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException;
    List<Map<String,Object>> getAuthorsMastersMap(int offset, int limit, Set<String> fields, String restrict) throws NoSuchEntityException, WrongRestrictionException;

    int createAuthor(AuthorView view) throws ServiceErrorException, NoSuchEntityException, ValidationException;

    int createAuthorMaster(AuthorMasterView view) throws ValidationException, ServiceErrorException, NoSuchEntityException;

    int updateAuthor(AuthorView authorView) throws ServiceErrorException, NoSuchEntityException, ValidationException;
    int updateAuthor(AuthorEntity author) throws ServiceErrorException, NoSuchEntityException, ValidationException;
    int updateAuthorMaster(AuthorMasterView view) throws ServiceErrorException, NoSuchEntityException, ValidationException;

    int countAuthors(String restrict) throws WrongRestrictionException;
    int countAuthorsMaster(String restrict) throws WrongRestrictionException;

    void deleteAuthor(int id) throws NoSuchEntityException;
    void deleteMasterAuthor(int id) throws NoSuchEntityException;

    AuthorMasterEntity findByNameMaster(String lastName,String initials);
    AuthorEntity findByOriginal(String original);

    List<Map<String,Object>> searchAuthors(Set<String> fields, String restrict) throws WrongRestrictionException, NoSuchEntityException;
}
