package com.eduhab.service;

import com.eduhab.domain.User;
import com.eduhab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() { return userRepository.findAll(); }

    @Override
    public User insert(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Override
    public User getById(int id) { return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id)); }

    @Override
    public User update(User user) {
        if (user.getPasswordHash() != null) {
            String hash = user.getPasswordHash();
            if (!hash.startsWith("$2a$")) {
                user.setPasswordHash(passwordEncoder.encode(hash));
            }
        } else {
            User existing = getById(user.getId());
            user.setPasswordHash(existing.getPasswordHash());
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(int id) { userRepository.deleteById(id); }
}
