package com.switchvov.magicrpc.demo.api;

/**
 * @author switch
 * @since 2024/3/6
 */
public interface UserService {
    User findById(int id);

    int getId(int id);

    String getName();
}
