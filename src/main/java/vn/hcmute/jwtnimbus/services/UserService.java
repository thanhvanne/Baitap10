package vn.hcmute.jwtnimbus.services;

import org.springframework.stereotype.Service;
import vn.hcmute.jwtnimbus.entity.User;
import vn.hcmute.jwtnimbus.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }
}