package com.hashedin.huspark.service.impl;

import com.hashedin.huspark.dto.ProviderProfileDTO;
import com.hashedin.huspark.model.ProviderProfile;
import com.hashedin.huspark.model.User;
import com.hashedin.huspark.repository.ProviderProfileRepository;
import com.hashedin.huspark.repository.UserRepository;
import com.hashedin.huspark.service.ProviderProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderProfileServiceImpl implements ProviderProfileService {

    private final ProviderProfileRepository providerProfileRepository;
    private final UserRepository userRepository;

    @Override
    public ProviderProfile createOrUpdateProfile(String email, ProviderProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProviderProfile profile = providerProfileRepository.findByUser(user)
                .orElse(ProviderProfile.builder().user(user).build());

        profile.setBio(dto.getBio());
        profile.setAddress(dto.getAddress());
        profile.setCity(dto.getCity());
        profile.setApproved(false);
        profile.setBlocked(false);

        return providerProfileRepository.save(profile);
    }

    @Override
    public ProviderProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return providerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @Override
    public List<ProviderProfile> getAllProfiles() {
        return providerProfileRepository.findAll();
    }

    @Override
    public ProviderProfile approveProvider(Long id) {
        ProviderProfile profile = providerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setApproved(true);
        return providerProfileRepository.save(profile);
    }

    @Override
    public ProviderProfile blockProvider(Long id, boolean block) {
        ProviderProfile profile = providerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setBlocked(block);
        return providerProfileRepository.save(profile);
    }
}
