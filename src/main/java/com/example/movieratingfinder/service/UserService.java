package com.example.movieratingfinder.service;

import com.example.movieratingfinder.model.Movie;
import com.example.movieratingfinder.model.User;
import com.example.movieratingfinder.repository.MovieRepository;
import com.example.movieratingfinder.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.verification-token.expiration}")
    private int verificationTokenExpiration;

    @Value("${app.reset-token.expiration}")
    private int resetTokenExpiration;

    @Transactional
    public User registerUser(User user) throws MessagingException, UnsupportedEncodingException {
        // Check if username exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (user.getEmail() != null && !user.getEmail().isEmpty() &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Set default values for new user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        user.setEnabled(false);
        user.setProvider("local");

        // Generate verification code
        String verificationCode = RandomStringUtils.randomAlphanumeric(64);
        user.setVerificationCode(verificationCode);

        // Save user
        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                verificationCode
        );

        return savedUser;
    }

    @Transactional
    public boolean verifyUser(String verificationCode) {
        Optional<User> userOpt = userRepository.findByVerificationCode(verificationCode);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Transactional
    public void initiatePasswordReset(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Generate reset token
            String resetToken = RandomStringUtils.randomAlphanumeric(64);
            user.setResetPasswordToken(resetToken);
            userRepository.save(user);

            // Send password reset email
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    user.getUsername(),
                    resetToken
            );
        } else {
            // For security reasons, don't reveal that the email doesn't exist
            // Just silently ignore the request
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Transactional
    public User processOAuthUser(String provider, String providerId, String email, String name) {
        // Check if user exists by provider and providerId
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Check if user exists with the same email
        Optional<User> userWithEmail = userRepository.findByEmail(email);

        if (userWithEmail.isPresent()) {
            // Link existing account with OAuth provider
            User user = userWithEmail.get();
            user.setProvider(provider);
            user.setProviderId(providerId);
            return userRepository.save(user);
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(email.split("@")[0] + "_" + provider); // Generate username from email
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(16))); // Random password
        newUser.setRoles("ROLE_USER");
        newUser.setEnabled(true); // OAuth users are pre-verified
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);

        return userRepository.save(newUser);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove user from all watchlists
        for (Movie movie : user.getWatchlist()) {
            movie.getUsers().remove(user);
        }

        userRepository.delete(user);
    }

    public void addToWatchlist(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // Add movie to watchlist if not already present
        if (!user.getWatchlist().contains(movie)) {
            user.addToWatchlist(movie);
            userRepository.save(user);
        }
    }

    public void removeFromWatchlist(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        user.removeFromWatchlist(movie);
        userRepository.save(user);
    }

    public Set<Movie> getWatchlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getWatchlist();
    }

    public boolean isInWatchlist(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getWatchlist().stream()
                .anyMatch(movie -> movie.getId().equals(movieId));
    }
}
