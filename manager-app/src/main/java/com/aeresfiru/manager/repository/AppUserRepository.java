package com.aeresfiru.manager.repository;

import com.aeresfiru.manager.entity.AppUser;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AppUserRepository extends Repository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);
}
