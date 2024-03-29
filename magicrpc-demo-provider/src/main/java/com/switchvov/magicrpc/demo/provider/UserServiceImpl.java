package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcException;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author switch
 * @since 2024/3/6
 */
@Service
@MagicProvider
public class UserServiceImpl implements UserService {

    @Autowired
    private Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "magic-" + environment.getProperty("server.port") + "-" + System.currentTimeMillis());
    }

    @Override
    public User findById(long id) {
        return new User(Long.valueOf(id).intValue(), "magic-" + environment.getProperty("server.port") + "-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "magic-" + name + "-" + System.currentTimeMillis());
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public long getId(float id) {
        return Float.valueOf(id).intValue();
    }

    @Override
    public int getId(int id) {
        return id;
    }

    @Override
    public String getName() {
        return "magic-123";
    }

    @Override
    public int[] getIds() {
        return new int[]{100, 200, 300};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1, 2, 3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public List<User> getList(List<User> users) {
        return users;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return !flag;
    }

    @Override
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public User ex(boolean flag) {
        if (flag) {
            throw new RpcException("just throw an exception");
        }
        return new User(100, "magic-" + environment.getProperty("server.port") + "-" + System.currentTimeMillis());
    }

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if ("8079".equals(port)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
        }
        return new User(1001, "magic-" + environment.getProperty("server.port") + "-" + System.currentTimeMillis());
    }

}
