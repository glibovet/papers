package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.AddressEntity;

import java.util.List;

/**
 * Created by Andrii on 02.10.2016.
 */
public interface AddressRepository extends JpaRepository<AddressEntity, Integer>{
    List<AddressEntity> findAll();
    Page<AddressEntity> findAll(org.springframework.data.domain.Pageable pageable);
}
