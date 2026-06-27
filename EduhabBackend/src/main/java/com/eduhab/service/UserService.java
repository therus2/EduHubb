package com.eduhab.service;

import com.eduhab.domain.User;
import java.util.List;

public interface UserService {
    User insert(User user);
    User getById(int id);
    List<User> getAll();
    User update(User user);
    void deleteById(int id);
}
