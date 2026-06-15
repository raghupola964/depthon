package com.depthon.service;

import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;
import com.depthon.model.User;
import com.depthon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final int MAX_DIVISIONS = 2;

    private final UserRepository userRepository;

    // Work out every division a user currently touches:
    // their home division + the division of each followed subdivision.
    private Set<Division> activeDivisions(User user) {
        Set<Division> divisions = new HashSet<>();
        divisions.add(user.getDivision());  // home division always counts
        for (Subdivision sub : user.getFollowedSubdivisions()) {
            divisions.add(sub.getDivision());
        }
        return divisions;
    }

    // Add a subdivision to follow, enforcing the 2-division cap.
    public User followSubdivision(String email, Subdivision toAdd) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Already following it? Nothing to do.
        if (user.getFollowedSubdivisions().contains(toAdd)) {
            return user;
        }

        // Work out what the active divisions WOULD be after adding this one.
        Set<Division> futureDivisions = activeDivisions(user);
        futureDivisions.add(toAdd.getDivision());

        if (futureDivisions.size() > MAX_DIVISIONS) {
            throw new RuntimeException(
                "You can follow at most " + MAX_DIVISIONS + " divisions. " +
                "Remove a subdivision from another division first.");
        }

        user.getFollowedSubdivisions().add(toAdd);
        return userRepository.save(user);
    }

    // Remove a followed subdivision.
    public User unfollowSubdivision(String email, Subdivision toRemove) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Rule: you cannot unfollow your own HOME field.
        if (user.getSubdivision() == toRemove) {
            throw new RuntimeException("You cannot unfollow your home field");
        }

        user.getFollowedSubdivisions().remove(toRemove);
        return userRepository.save(user);
    }
}