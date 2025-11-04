package com.hashedin.huspark.service.impl;

import com.hashedin.huspark.dto.ProviderProfileDTO;
import com.hashedin.huspark.model.ProviderProfile;
import com.hashedin.huspark.model.User;
import com.hashedin.huspark.repository.ProviderProfileRepository;
import com.hashedin.huspark.service.ProviderProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderProfileServiceImpl implements ProviderProfileService {

    private final ProviderProfileRepository providerProfileRepository;

    @Override
    public ProviderProfile createOrUpdateProfile(User user, ProviderProfileDTO profileDTO) {
        ProviderProfile profile = providerProfileRepository.findByUser(user)
                .orElse(ProviderProfile.builder()
                        .user(user)
                        .approved(false)
                        .blocked(false)
                        .rating(0.0)
                        .totalReviews(0)
                        .build());

        profile.setBio(profileDTO.getBio());
        profile.setAddress(profileDTO.getAddress());
        profile.setCity(profileDTO.getCity());

        return providerProfileRepository.save(profile);
    }

    @Override
    public ProviderProfile getProfileByUser(User user) {
        return providerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Provider profile not found for user: " + user.getEmail()));
    }

    @Override
    public List<ProviderProfile> getAllProfiles() {
        return providerProfileRepository.findAll();
    }

    @Override
    public ProviderProfile approveProvider(Long id) {
        ProviderProfile profile = providerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        profile.setApproved(true);
        return providerProfileRepository.save(profile);
    }

    @Override
    public ProviderProfile blockProvider(Long id, boolean block) {
        ProviderProfile profile = providerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        profile.setBlocked(block);
        return providerProfileRepository.save(profile);
    }
}
