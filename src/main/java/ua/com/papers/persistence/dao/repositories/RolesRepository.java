package ua.com.papers.persistence.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.papers.pojo.entities.RoleEntity;
import ua.com.papers.pojo.enums.RolesEnum;

/**
 * Created by Andrii on 10.09.2016.
 */
public interface RolesRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findByName(RolesEnum name);
}
