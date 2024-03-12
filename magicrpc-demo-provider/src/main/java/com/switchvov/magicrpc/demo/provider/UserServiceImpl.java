package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

/**
 * @author switch
 * @since 2024/3/6
 */
@Service
@MagicProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "magic-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "magic-" + name + "-" + System.currentTimeMillis());
    }

    @Override
    public int getId(int id) {
        return id;
    }

    @Override
    public int getId(long id) {
        return id > Integer.MAX_VALUE ? -1 : (int) id;
    }

    @Override
    public int getId(User user) {
        return user.getId();
    }

    @Override
    public String getName() {
        return "magic-123";
    }
}
