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
}
