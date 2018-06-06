package ua.com.papers.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.com.papers.convertors.Converter;
import ua.com.papers.criteria.impl.UserCriteria;
import ua.com.papers.exceptions.bad_request.WrongPasswordException;
import ua.com.papers.exceptions.conflict.EmailExistsException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.criteria.ICriteriaRepository;
import ua.com.papers.persistence.dao.repositories.ContactsRepository;
import ua.com.papers.persistence.dao.repositories.RolesRepository;
import ua.com.papers.persistence.dao.repositories.UsersRepository;
import ua.com.papers.pojo.entities.ContactEntity;
import ua.com.papers.pojo.entities.PermissionEntity;
import ua.com.papers.pojo.entities.RoleEntity;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.UserView;
import ua.com.papers.services.utils.SessionUtils;
import ua.com.papers.storage.IStorageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by Andrii on 18.08.2016.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UsersRepository usersRepository;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private IUserValidateService userValidateService;

    @Resource
    private RolesRepository rolesRepository;

    @Autowired
    private Converter<UserEntity> userConverter;

    @Autowired
    private ICriteriaRepository criteriaRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private IStorageService storageService;

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
    /**
     * offset number of rows to skip
     * limit max on request
     */
    public List<UserEntity> getUsers(int offset, int limit) throws NoSuchEntityException {
        Page<UserEntity> list = usersRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("user", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    public List<UserEntity> getUsers(UserCriteria criteria) throws NoSuchEntityException {
        List<UserEntity> users = criteriaRepository.find(criteria);
        if (users == null || users.isEmpty()) {
            throw new NoSuchEntityException("users");
        }

        return users;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getUsersMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return userConverter.convert(getUsers(offset, limit), fields);
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
    public int create(UserView view) throws EmailExistsException, ServiceErrorException, ValidationException {
        try {
            getByEmail(view.getEmail());
            // should be exception
            // otherwise user exists and exception should be thrown
            throw new EmailExistsException();
        } catch (NoSuchEntityException e) {
            UserEntity entity = new UserEntity();
            if (view.getRole() == null)
                view.setRole(RolesEnum.user);
            view.setActive(true);
            merge(entity, view);
            userValidateService.validForCreate(view);
            entity = usersRepository.saveAndFlush(entity);
            if(entity == null){
                throw new ServiceErrorException();
            }
            entity.setActive(true);
            entity = usersRepository.saveAndFlush(entity);
            if (!sessionUtils.isAuthorized()){
                sessionUtils.logeInUser(entity);
            }
            return entity.getId();
        }
    }

    @Override
    @Transactional(rollbackFor=NoSuchEntityException.class)
    public UserEntity update(UserView userView) throws NoSuchEntityException {
        UserEntity updatedUser = usersRepository.findOne(userView.getId());
        if (updatedUser == null)
            throw new NoSuchEntityException(UserEntity.class.getName(),"userId"+userView.getId());
        updatedUser.setName(userView.getName());
        updatedUser.setLastName(userView.getLastName());
        updatedUser.setRoleEntity(rolesRepository.findByName(userView.getRole()));
        if(userView.getEmail() != null) {
            updatedUser.setEmail(userView.getEmail());
        }
        //TODO add oll other
        usersRepository.saveAndFlush(updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional(rollbackFor=NoSuchEntityException.class)
    public UserEntity update(UserEntity user) {
        usersRepository.saveAndFlush(user);
        return user;
    }

    @Override
    @Transactional
    public List<UserEntity> findByNames(String name, String lastName) {
        name = name == null ? "" : name;
        lastName = lastName == null ? "" : lastName;
        return usersRepository.findByNameIgnoreCaseContainingAndLastNameIgnoreCaseContaining(name, lastName);
    }

    @Override
    @Transactional
    public boolean signInUser(UserView view) throws NoSuchEntityException, WrongPasswordException {
        UserEntity entity = getByEmail(view.getEmail());
        if(!entity.getPassword().equals(view.getPassword()))
            throw new WrongPasswordException();

        Authentication authentication = new UsernamePasswordAuthenticationToken(entity, entity.getPassword(), getGrantedAuthorities(entity));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        return true;
    }

    @Override
    public boolean logoutUser(HttpServletRequest request, HttpServletResponse response) {
        CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        cookieClearingLogoutHandler.logout(request, response, null);
        securityContextLogoutHandler.logout(request, response, null);
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return true;
    }

    private List<GrantedAuthority> getGrantedAuthorities(UserEntity user){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (PermissionEntity perm : user.getRoleEntity().getPermissions()){
            authorities.add(new SimpleGrantedAuthority(perm.getName()));
        }
        return authorities;
    }

    public void merge(UserEntity entity, UserView view){
        if(view.getName() != null)
            entity.setName(view.getName());
        else view.setName(entity.getName());

        if(view.getLastName() != null)
            entity.setLastName(view.getLastName());
        else view.setLastName(entity.getLastName());

        if(view.getEmail() != null)
            entity.setEmail(view.getEmail());
        else view.setEmail(entity.getEmail());

        if(view.getActive() != null)
            entity.setActive(view.getActive());
        else view.setActive(entity.isActive());

        if(view.getPassword() != null)
            entity.setPassword(view.getPassword());
        else view.setPassword(entity.getPassword());

        if (view.getRole() != null){
            RoleEntity role = rolesRepository.findByName(view.getRole());
            entity.setRoleEntity(role);
        }else if (entity.getRoleEntity()!=null){
            view.setRole(entity.getRoleEntity().getName());
        }
    }

    @Override
    public Set<UserEntity> getAcceptedContacts (UserEntity user){
        Set<UserEntity> result = new HashSet<>();
        Set<ContactEntity> sentContactRequests = user.getSentContactRequests();
        for(ContactEntity c:sentContactRequests){
            if(c.isAccepted()){
                result.add(c.getUserTo());
            }
        }
        Set<ContactEntity> receivedContactRequests = user.getReceivedContactRequests();
        for(ContactEntity c:receivedContactRequests){
            if(c.isAccepted()){
                result.add(c.getUserFrom());
            }
        }
        System.out.println("result " + result);
        return result;
    }

    @Override
    public boolean isConnected (int firstId, int secondId) throws NoSuchEntityException {
        UserEntity first = getUserById(firstId);
        UserEntity second = getUserById(secondId);
        Set<UserEntity> acceptedContacts = getAcceptedContacts(first);
        for(UserEntity userEntity: acceptedContacts){
            if(userEntity.getId() == second.getId()){
                return true;
            }
        }
        return false;
    }

    @Override
    public ContactEntity createContactRequest (UserEntity userFrom, UserEntity userTo, String message, MultipartFile attachment) throws IOException, ServiceErrorException {
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setUserFrom(userFrom);
        contactEntity.setUserTo(userTo);
        contactEntity.setAccepted(false);
        contactEntity.setMessage(message);
        contactEntity = contactsRepository.saveAndFlush(contactEntity);
        if(!attachment.isEmpty()){
            storageService.uploadRequestAttachment(contactEntity, attachment);
        }
        return contactEntity;
    }

    @Override
    @Transactional(rollbackFor=NoSuchEntityException.class)
    public ContactEntity update(ContactEntity contact) {
        contactsRepository.saveAndFlush(contact);
        return contact;
    }

    @Override
    public void deleteContact(ContactEntity contact) {
        if(contact == null) return;
        contactsRepository.delete(contact);
    }

    public ContactEntity getContactByUsers(UserEntity userFrom, UserEntity userTo){
        ContactEntity contact = contactsRepository.findByUserFromAndUserTo(userFrom, userTo);
        if(contact == null) contact = contactsRepository.findByUserFromAndUserTo(userTo, userFrom);
        return contact;
    }

    public void acceptContactRequest (ContactEntity contact){
        if(contact == null) return;
        contact.setAccepted(true);
        contactsRepository.saveAndFlush(contact);
    }

    public ContactEntity getContactById (int contactId){
        return contactsRepository.findOne(contactId);
    }

    public List<ContactEntity> getReceivedContactRequests(UserEntity user){
        List<ContactEntity> contacts = contactsRepository.findByUserToAndIsAccepted(user, false);
        System.out.println("getReceivedContactRequests "+contacts);
        return contacts;
    }
}
