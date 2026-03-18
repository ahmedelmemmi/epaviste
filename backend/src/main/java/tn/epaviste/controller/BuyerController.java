package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.BuyerProfileRequest;
import tn.epaviste.dto.request.VehicleRequest;
import tn.epaviste.dto.response.BuyerStatsResponse;
import tn.epaviste.dto.response.UserResponse;
import tn.epaviste.dto.response.VehicleResponse;
import tn.epaviste.entity.User;
import tn.epaviste.service.BuyerService;

import java.util.List;

@Tag(name = "Buyer", description = "Buyer dashboard endpoints")
@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @Operation(summary = "Get buyer dashboard statistics")
    @GetMapping("/dashboard")
    public ResponseEntity<BuyerStatsResponse> getDashboardStats(Authentication authentication) {
        return ResponseEntity.ok(buyerService.getStats(authentication.getName()));
    }

    @Operation(summary = "Get buyer profile")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        User user = buyerService.getProfile(authentication.getName());
        return ResponseEntity.ok(toUserResponse(user));
    }

    @Operation(summary = "Update buyer profile")
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody BuyerProfileRequest request,
            Authentication authentication) {
        User user = buyerService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @Operation(summary = "Get saved vehicles (garage)")
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getVehicles(Authentication authentication) {
        return ResponseEntity.ok(buyerService.getVehicles(authentication.getName()));
    }

    @Operation(summary = "Add a vehicle to the garage")
    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(buyerService.addVehicle(authentication.getName(), request));
    }

    @Operation(summary = "Delete a vehicle from the garage")
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long id,
            Authentication authentication) {
        buyerService.deleteVehicle(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
