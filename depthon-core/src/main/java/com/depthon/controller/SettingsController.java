package com.depthon.controller;

import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;
import com.depthon.model.User;
import com.depthon.service.SettingsService;
import com.depthon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;
    private final UserService userService;

    // GET /api/settings/follows - what I follow + my active divisions + cap status
    @GetMapping("/follows")
    public ResponseEntity<Map<String, Object>> getFollows(Principal principal) {
        User user = userService.findByEmail(principal.getName());

        // Build the set of active divisions (home + each followed subdivision's division)
        Set<Division> activeDivisions = new HashSet<>();
        activeDivisions.add(user.getDivision());
        for (Subdivision sub : user.getFollowedSubdivisions()) {
            activeDivisions.add(sub.getDivision());
        }

        return ResponseEntity.ok(Map.of(
                "homeSubdivision", user.getSubdivision(),
                "homeDivision", user.getDivision(),
                "followedSubdivisions", user.getFollowedSubdivisions(),
                "activeDivisions", activeDivisions,
                "divisionsUsed", activeDivisions.size(),
                "maxDivisions", 2
        ));
    }

    // POST /api/settings/follow  body: { "subdivision": "DATA_ANALYST" }
    @PostMapping("/follow")
    public ResponseEntity<Map<String, Object>> follow(
            @RequestBody Map<String, String> body,
            Principal principal) {
        Subdivision toAdd = Subdivision.valueOf(body.get("subdivision"));
        User user = settingsService.followSubdivision(principal.getName(), toAdd);
        return ResponseEntity.ok(Map.of(
                "message", "Now following " + toAdd.getDisplayName(),
                "followedSubdivisions", user.getFollowedSubdivisions()
        ));
    }

    // POST /api/settings/unfollow  body: { "subdivision": "DATA_ANALYST" }
    @PostMapping("/unfollow")
    public ResponseEntity<Map<String, Object>> unfollow(
            @RequestBody Map<String, String> body,
            Principal principal) {
        Subdivision toRemove = Subdivision.valueOf(body.get("subdivision"));
        User user = settingsService.unfollowSubdivision(principal.getName(), toRemove);
        return ResponseEntity.ok(Map.of(
                "message", "Unfollowed " + toRemove.getDisplayName(),
                "followedSubdivisions", user.getFollowedSubdivisions()
        ));
    }
}