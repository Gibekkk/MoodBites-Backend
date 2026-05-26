package com.moodbites.restfulapi.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.moodbites.restfulapi.model.enums.Flavor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_flavor_preferences")
public class UserFlavorPreference {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_preference_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_userFlavorPreferenceUserPreference"))
    private UserPreference userPreferenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "flavor", nullable = false)
    private Flavor flavor;

    @Max(5)
    @Min(1)
    @Column(name = "preference_scale", nullable = true)
    private Integer preferenceScale;

    @Max(5)
    @Min(1)
    @Column(name = "intensity_scale", nullable = true)
    private Integer intensityScale;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @OneToMany(mappedBy = "userFlavorPreferenceId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFlavorFoodPreference> userFlavorFoodPreferences;

}
