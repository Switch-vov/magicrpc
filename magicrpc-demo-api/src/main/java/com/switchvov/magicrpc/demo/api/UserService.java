package com.switchvov.magicrpc.demo.api;

/**
 * @author switch
 * @since 2024/3/6
 */
public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    int getId(int id);

    int getId(long id);

    int getId(User user);

    String getName();
}
