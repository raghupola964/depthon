package com.depthon.service;

import com.depthon.domain.Subdivision;
import com.depthon.model.User;
import com.depthon.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SettingsService settingsService;

    // Helper: build a user whose home field is Software Developer (IT division)
    private User makeSoftwareDeveloper() {
        User user = new User();
        user.setEmail("dev@depthon.com");
        user.setSubdivision(Subdivision.SOFTWARE_DEVELOPER);        // home = IT
        user.setDivision(Subdivision.SOFTWARE_DEVELOPER.getDivision());
        user.setFollowedSubdivisions(new HashSet<>());
        return user;
    }

    @Test
    void followSubdivision_withinSameDivision_succeeds() {
        // ARRANGE: a Software Developer (IT), following nothing yet
        User user = makeSoftwareDeveloper();
        when(userRepository.findByEmail("dev@depthon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(c -> c.getArgument(0));

        // ACT: follow DATA_ANALYST (also IT - still just 1 division)
        settingsService.followSubdivision("dev@depthon.com", Subdivision.DATA_ANALYST);

        // ASSERT: the follow was added
        assertTrue(user.getFollowedSubdivisions().contains(Subdivision.DATA_ANALYST));
    }

    @Test
    void followSubdivision_secondDivision_succeeds() {
        // ARRANGE: Software Developer (IT)
        User user = makeSoftwareDeveloper();
        when(userRepository.findByEmail("dev@depthon.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(c -> c.getArgument(0));

        // ACT: follow DATA_SCIENTIST (AI - now 2 divisions, IT + AI: allowed)
        settingsService.followSubdivision("dev@depthon.com", Subdivision.DATA_SCIENTIST);

        // ASSERT: allowed
        assertTrue(user.getFollowedSubdivisions().contains(Subdivision.DATA_SCIENTIST));
    }

    /* @Test
    void followSubdivision_thirdDivision_isBlocked() {
        // ARRANGE: a Software Developer (IT) who ALREADY follows an AI field
        User user = makeSoftwareDeveloper();
        user.getFollowedSubdivisions().add(Subdivision.DATA_SCIENTIST);  // now spans IT + AI (2)
        when(userRepository.findByEmail("dev@depthon.com")).thenReturn(Optional.of(user));

        // ACT + ASSERT: following a 3rd division must throw
        // (NOTE: this assumes a 3rd division exists in your taxonomy. See note below.)
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            settingsService.followSubdivision("dev@depthon.com", Subdivision.SOFTWARE_DEVELOPER);
        });
    } */

    @Test
    void unfollowSubdivision_homeField_isBlocked() {
        // ARRANGE: a Software Developer
        User user = makeSoftwareDeveloper();
        when(userRepository.findByEmail("dev@depthon.com")).thenReturn(Optional.of(user));

        // ACT + ASSERT: trying to unfollow your OWN home field must throw
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            settingsService.unfollowSubdivision("dev@depthon.com", Subdivision.SOFTWARE_DEVELOPER);
        });
        assertTrue(thrown.getMessage().contains("home"));
    }
}