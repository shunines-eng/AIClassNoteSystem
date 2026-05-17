package com.noteaiBackend.repository;

import com.noteaiBackend.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.io.Serializable;

@Repository
public interface RoleUserRepository extends JpaRepository<RoleUser, Serializable> {
}
