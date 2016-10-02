package ua.com.papers.services.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.convertors.Converter;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.persistence.dao.repositories.AddressRepository;
import ua.com.papers.pojo.entities.AddressEntity;
import ua.com.papers.pojo.view.AddressView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Service
public class AddressServiceImpl implements IAddressService{

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private Converter<AddressEntity> addressConverter;
    @Autowired
    private IAddressValidationService addressValidationService;

    @Override
    @Transactional
    public AddressEntity getAddressById(int id) throws NoSuchEntityException {
        AddressEntity entity = addressRepository.findOne(id);
        if (entity == null)
            throw new NoSuchEntityException("address","id:"+id);
        return entity;
    }

    @Override
    @Transactional
    public List<AddressEntity> getAddress(int offset, int limit) throws NoSuchEntityException {
        if (limit==0)
            limit=20;
        Page<AddressEntity> list = addressRepository.findAll(new PageRequest(offset/limit,limit));
        if(list == null || list.getContent().isEmpty())
            throw new NoSuchEntityException("address", String.format("[offset: %d, limit: %d]", offset, limit));
        return list.getContent();
    }

    @Override
    public Map<String, Object> getAddressMapById(int id, Set<String> fields) throws NoSuchEntityException {
        return addressConverter.convert(getAddressById(id),fields);
    }

    @Override
    public List<Map<String, Object>> getAddressMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException {
        return addressConverter.convert(getAddress(offset, limit),fields);
    }

    @Override
    public int createAddress(AddressView view) throws ValidationException, ServiceErrorException {
        AddressEntity entity = new AddressEntity();
        merge(entity,view);
        addressValidationService.addressValidForCreation(entity);
        entity= addressRepository.saveAndFlush(entity);
        if(entity == null){
            throw new ServiceErrorException();
        }
        return entity.getId();
    }

    private void merge(AddressEntity entity, AddressView view) {
        if (view.getId()!=null) entity.setId(view.getId());
        else view.setId(entity.getId());
        if (view.getAddress()!=null&&!"".equals(view.getAddress())) entity.setAddress(view.getAddress());
        else view.setAddress(entity.getAddress());
        if (view.getCity()!=null&&!"".equals(view.getCity())) entity.setCity(view.getCity());
        else view.setCity(entity.getCity());
        if (view.getCountry()!=null&&!"".equals(view.getCountry())) entity.setCountry(view.getCountry());
        else view.setCountry(entity.getCountry());
    }

    @Override
    public int updateAddress(AddressView view) throws ServiceErrorException, NoSuchEntityException, ValidationException {
        if (view.getId()==null||view.getId()==0)
            throw new ServiceErrorException();
        AddressEntity authorEntity = getAddressById(view.getId());
        merge(authorEntity,view);
        addressValidationService.addressValidForUpdate(authorEntity);
        authorEntity = addressRepository.saveAndFlush(authorEntity);
        if(authorEntity == null){
            throw new ServiceErrorException();
        }
        return authorEntity.getId();
    }

}
