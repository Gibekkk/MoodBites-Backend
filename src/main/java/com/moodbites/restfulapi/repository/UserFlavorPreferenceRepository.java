package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.UserFlavorPreference;

import java.util.Optional;

public interface UserFlavorPreferenceRepository extends JpaRepository<UserFlavorPreference, String> {
}
