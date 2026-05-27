package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, String> {
}
