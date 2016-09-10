package ua.com.papers.services.users;

import ua.com.papers.exceptions.conflict.EmailExistsException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.view.UserView;

import java.util.*;

/**
 * Created by Andrii on 18.08.2016.
 */
public interface IUserService {

    UserEntity getUserById(int userId) throws NoSuchEntityException;

    Map<String, Object> getUserByIdMap(int userId, Set<String> fields) throws NoSuchEntityException;

    UserEntity getByEmail(String email) throws NoSuchEntityException;

    int create(UserView view) throws EmailExistsException, ServiceErrorException, ValidationException;

    UserEntity update(UserEntity user) throws NoSuchEntityException;

}
