package com.moodbites.restfulapi.service;

import com.moodbites.restfulapi.repository.UserSampleFoodPreferenceRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moodbites.restfulapi.dto.MoodFormDTO;
import com.moodbites.restfulapi.dto.constructs.FlavorProfile;
import com.moodbites.restfulapi.dto.constructs.MoodProfile;
import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.model.UserFlavorPreference;
import com.moodbites.restfulapi.model.UserPreference;
import com.moodbites.restfulapi.model.UserSampleFoodPreference;
import com.moodbites.restfulapi.model.enums.Flavor;
import com.moodbites.restfulapi.model.enums.Mood;
import com.moodbites.restfulapi.model.enums.SampleFood;
import com.moodbites.restfulapi.repository.UserPreferenceRepository;
import com.moodbites.restfulapi.repository.UserFlavorPreferenceRepository;

@Service
public class FormService {

    @Autowired
    private UserSampleFoodPreferenceRepository userSampleFoodPreferenceRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserFlavorPreferenceRepository userFlavorPreferenceRepository;

    public void createUserPreferences(User user) {
        createUserMoodPreferences(user);
        createUserFlavorPreferences(user);
    }

    public void createUserMoodPreferences(User user) {
        for (Mood mood : Mood.getMoodList()) {
            UserPreference userPreference = new UserPreference();
            userPreference.setUserId(user);
            userPreference.setMood(mood);
            userPreferenceRepository.save(userPreference);
        }
    }

    public void createUserFlavorPreferences(User user) {
        for (UserPreference userPreference : user.getUserPreferences()) {
            for (Flavor flavor : Flavor.getFlavorList()) {
                UserFlavorPreference userFlavorPreference = new UserFlavorPreference();
                userFlavorPreference.setUserPreferenceId(userPreference);
                userFlavorPreference.setFlavor(flavor);
                userFlavorPreference.setEditedAt(LocalDateTime.now());
                userFlavorPreferenceRepository.save(userFlavorPreference);
            }
        }
    }

    public void updateUserPreferences(User user, MoodFormDTO moodFormDTO) {
        Map<String, MoodProfile> moodProfiles = moodFormDTO.getMoods();
        for (UserPreference userPreference : user.getUserPreferences()) {
            MoodProfile moodProfile = moodProfiles.get(userPreference.getMood().toString());
            FlavorProfile desires = moodProfile.getDesire();
            FlavorProfile intensities = moodProfile.getIntensity();
            List<String> categories = moodProfile.getCategories();
            for (UserFlavorPreference userFlavorPreference : userPreference.getUserFlavorPreferences()) {
                switch (userFlavorPreference.getFlavor()) {
                    case SWEET -> {
                        userFlavorPreference.setPreferenceScale(desires.getManis());
                        userFlavorPreference.setIntensityScale(intensities.getManis());
                    }
                    case SOUR -> {
                        userFlavorPreference.setPreferenceScale(desires.getAsamSegar());
                        userFlavorPreference.setIntensityScale(intensities.getAsamSegar());
                    }
                    case SALTY -> {
                        userFlavorPreference.setPreferenceScale(desires.getAsinGurih());
                        userFlavorPreference.setIntensityScale(intensities.getAsinGurih());
                    }
                    case BITTER -> {
                        userFlavorPreference.setPreferenceScale(desires.getPahit());
                        userFlavorPreference.setIntensityScale(intensities.getPahit());
                    }
                    case SPICY -> {
                        userFlavorPreference.setPreferenceScale(desires.getPedas());
                        userFlavorPreference.setIntensityScale(intensities.getPedas());
                    }
                }
                userFlavorPreference.setEditedAt(LocalDateTime.now());
                userFlavorPreferenceRepository.save(userFlavorPreference);
            }

            clearUserSampleFoodPreferences(userPreference);
            if (categories != null && !categories.isEmpty()) {
                for (String category : categories) {
                    UserSampleFoodPreference userSampleFoodPreference = new UserSampleFoodPreference();
                    userSampleFoodPreference.setUserPreferenceId(userPreference);
                    userSampleFoodPreference.setSampleFood(SampleFood.fromString(category));
                    userSampleFoodPreference.setCreatedAt(LocalDateTime.now());
                    userSampleFoodPreferenceRepository.save(userSampleFoodPreference);
                }
            }
        }
    }

    @Transactional
    public void clearUserSampleFoodPreferences(UserPreference userPreference) {
        userSampleFoodPreferenceRepository.deleteAllByUserPreferenceId(userPreference);
    }
}
