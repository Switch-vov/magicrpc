package com.switchvov.magicrpc.demo.api;

import java.util.List;
import java.util.Map;

/**
 * @author switch
 * @since 2024/3/6
 */
public interface UserService {
    User findById(int id);
    User findById(long id);

    User findById(int id, String name);

    long getId(long id);

    long getId(User user);

    long getId(float id);

    int getId(int id);

    String getName();

    int[] getIds();

    long[] getLongIds();

    int[] getIds(int[] ids);

    List<User> getList(List<User> users);

    Map<String, User> getMap(Map<String, User> userMap);

    Boolean getFlag(boolean flag);

    User[] findUsers(User[] users);
    User ex(boolean flag);

    public User find(int timeout);
}
