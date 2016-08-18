package ua.com.papers.services.users;

import ua.com.papers.persistence.entities.UserEntity;
import ua.com.papers.utils.exceptions.NoSuchEntityException;

/**
 * Created by Andrii on 18.08.2016.
 */
public interface IUserService {

    UserEntity getUserById(int userId) throws NoSuchEntityException;

    UserEntity getByEmail(String email) throws NoSuchEntityException;

    UserEntity create(UserEntity user);

    UserEntity update(UserEntity user) throws NoSuchEntityException;

}
