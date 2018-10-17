package ua.com.papers.services.users;

import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.criteria.impl.UserCriteria;
import ua.com.papers.exceptions.bad_request.WrongPasswordException;
import ua.com.papers.exceptions.conflict.EmailExistsException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.view.UserView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Andrii on 18.08.2016.
 */
public interface IUserService {

    UserEntity getUserById(int userId) throws NoSuchEntityException;

    Map<String, Object> getUserByIdMap(int userId, Set<String> fields) throws NoSuchEntityException;

    List<UserEntity> getUsers(int offset, int limit) throws NoSuchEntityException;

    List<UserEntity> getUsers(UserCriteria criteria) throws NoSuchEntityException;

    List<Map<String, Object>> getUsersMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException;

    UserEntity getByEmail(String email) throws NoSuchEntityException;

    int create(UserView view) throws EmailExistsException, ServiceErrorException, ValidationException;

    UserEntity update(UserView user) throws NoSuchEntityException;

    UserEntity update(UserEntity user);

    List<UserEntity> findByNames(String name, String lastName);

    boolean signInUser(UserView view) throws NoSuchEntityException, WrongPasswordException;

    boolean logoutUser(HttpServletRequest request, HttpServletResponse response);

    Set<UserEntity> getAcceptedContacts (UserEntity user);

    boolean isConnected (int firstId, int secondId) throws NoSuchEntityException;

    ContactEntity createContactRequest (UserEntity userFrom, UserEntity userTo, String message, MultipartFile attachment) throws IOException, ServiceErrorException;

    ContactEntity update(ContactEntity contact);

    void deleteContact(ContactEntity contact);

    ContactEntity getContactByUsers(UserEntity userFrom, UserEntity userTo);

    void acceptContactRequest (ContactEntity contact) throws IOException;

    ContactEntity getContactById (int contactId);

    List<ContactEntity> getReceivedContactRequests(UserEntity user);
}
