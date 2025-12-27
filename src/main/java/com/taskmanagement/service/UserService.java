    package com.taskmanagement.service;

    import java.time.LocalDateTime;

    import com.taskmanagement.model.Role;
    import com.taskmanagement.model.User;
    import com.taskmanagement.repository.UserRepository;
    import com.taskmanagement.utils.CurrentUser;

    public class UserService {

        private final UserRepository userRepository;

        public UserService() {
            this.userRepository = new UserRepository();
        }

        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
        public User register(String username, String email, String password) {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (password == null || password.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }

            if (userRepository.findByUsername(username.trim()) != null) {
                throw new IllegalArgumentException("Username already exists");
            }

            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setEmail(email != null ? email.trim() : null);
            newUser.setPasswordHash(password);
            newUser.setRole(Role.USER);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
        }

        public User login(String username, String password) {
            if (username == null || username.trim().isEmpty() || password == null) {
                throw new IllegalArgumentException("Username and password are required");
            }

            User user = userRepository.findByUsername(username.trim());
            if (user == null || !password.equals(user.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            userRepository.updateLastLogin(user);
            user = userRepository.findById(user.getId());
            CurrentUser .set(user);
            CurrentUser.updateLastLogin(user);
            
            return user;
        }

        public void logout() {CurrentUser.clear();}

        public User updateProfile(User updatedUser) {
            if (updatedUser == null || updatedUser.getId() == null) {
                throw new IllegalArgumentException("Invalid user data");
            }

            User existing = userRepository.findById(updatedUser.getId());
            if (existing == null) {
                throw new IllegalArgumentException("User not found");
            }
            existing.setEmail(updatedUser.getEmail());
            return userRepository.save(existing);
        }

        public void changePassword(User user, String oldPassword, String newPassword) {
            if (!oldPassword.equals(user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            if (newPassword == null || newPassword.length() < 8) {
                throw new IllegalArgumentException("New password must be at least 8 characters");
            }
            user.setPasswordHash(newPassword);
            userRepository.save(user);
        }

        public User updateUserRole(Long userId, Role newRole) {
            if (!CurrentUser.isAdmin()) {
                throw new SecurityException("Only admins can change user roles");
            }
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            user.setRole(newRole);
            return userRepository.save(user);
        }
        public boolean isUsernameTaken(String username) {
            return userRepository.findByUsername(username.trim()) != null;
        }

        public User getCurrentUser() {
            return CurrentUser.getInstance();
        }

        public java.util.List<User> getAllUsers() {
            if (!CurrentUser.isAdmin()) {
                throw new SecurityException("Only admins can view all users");
            }
            return userRepository.findAll();
        }
    }