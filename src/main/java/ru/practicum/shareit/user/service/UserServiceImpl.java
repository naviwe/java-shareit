package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user) {
        validate(user);
        if (getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new EmailException("User with email '" + user.getEmail() + "' already exists.");
        }
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        if (getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new EmailException("User with ID #" + user.getEmail() + " is already exist.");
        }
        var userToUpdate = get(user.getId());
        if (user.getName() != null) userToUpdate.setName(user.getName());
        if (user.getEmail() != null) userToUpdate.setEmail(user.getEmail());
        return userStorage.update(userToUpdate);
    }


    @Override
    public User get(Long id) {
        if (id == null) throw new ValidationException("User ID cannot be null.");
        return userStorage.get(id)
                .orElseThrow(() -> new NotFoundException("User with ID #" + id + " does not exist."));
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new ValidationException("User ID cannot b null.");
        if (!userStorage.get(id).isPresent()) {
            throw new NotFoundException("User with ID #" + id + " does not exist.");
        }
        userStorage.delete(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email: " + user.getEmail());
        }
    }
}
