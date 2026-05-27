package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.model.UserSampleFoodPreference;


public interface UserSampleFoodPreferenceRepository extends JpaRepository<UserSampleFoodPreference, String> {
    void deleteAllByUserId(User userId);
}
