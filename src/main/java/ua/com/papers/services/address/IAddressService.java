package ua.com.papers.services.address;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AddressEntity;
import ua.com.papers.pojo.view.AddressView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface IAddressService {
    AddressEntity getAddressById(int id) throws NoSuchEntityException;
    List<AddressEntity> getAddress(int offset, int limit) throws NoSuchEntityException;
    Map<String, Object> getAddressMapById(int id, Set<String> fields) throws NoSuchEntityException;
    List<Map<String,Object>> getAddressMap(int offset, int limit, Set<String> fields) throws NoSuchEntityException;
    int createAddress(AddressView view) throws ValidationException, ServiceErrorException;
    int updateAddress(AddressView view) throws ServiceErrorException, NoSuchEntityException, ValidationException;
}
