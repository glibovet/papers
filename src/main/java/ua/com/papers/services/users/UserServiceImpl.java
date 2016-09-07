package ua.com.papers.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.persistence.dao.repositories.UsersRepository;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 18.08.2016.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UsersRepository usersRepository;

    @Autowired
    private Converter<UserEntity> userConverter;

    @Override
    @Transactional
    public UserEntity getUserById(int userId) throws NoSuchEntityException {
        UserEntity user = usersRepository.findOne(userId);
        if (user == null)
            throw new NoSuchEntityException("user", "id: " + userId);
        return user;
    }

    @Override
    @Transactional
    public Map<String, Object> getUserByIdMap(int userId, Set<String> fields) throws NoSuchEntityException {
        return userConverter.convert(getUserById(userId), fields);
    }

    @Override
    @Transactional
    public UserEntity getByEmail(String email) throws NoSuchEntityException {
        UserEntity find = usersRepository.findByEmail(email);
        if (find == null)
            throw new NoSuchEntityException(UserEntity.class.getName(),"user email "+email);
        return find;
    }

    @Override
    @Transactional
    public UserEntity create(UserEntity user) {
        UserEntity created = user;
        return usersRepository.saveAndFlush(created);
    }

    @Override
    @Transactional(rollbackFor=NoSuchEntityException.class)
    public UserEntity update(UserEntity user) throws NoSuchEntityException {
        UserEntity updatedUser = usersRepository.findOne(user.getId());
        if (updatedUser == null)
            throw new NoSuchEntityException(UserEntity.class.getName(),"userId"+user.getId());
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        //TODO add oll other
        return updatedUser;

    }
}
