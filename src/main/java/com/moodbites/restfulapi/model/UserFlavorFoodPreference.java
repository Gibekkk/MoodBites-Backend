package com.moodbites.restfulapi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_flavor_food_preferences")
public class UserFlavorFoodPreference {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_flavor_preference_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_userFlavorFoodPreferenceUserFlavorPreference"))
    private UserFlavorPreference userFlavorPreferenceId;

    @ManyToOne
    @JoinColumn(nullable = false, name = "sample_food_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_userFlavorFoodPreferenceSampleFoods"))
    private SampleFoods sampleFoodId;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

}
