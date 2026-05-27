package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.UserSampleFoodPreference;

import java.util.Optional;

public interface UserSampleFoodPreferenceRepository extends JpaRepository<UserSampleFoodPreference, String> {
}
