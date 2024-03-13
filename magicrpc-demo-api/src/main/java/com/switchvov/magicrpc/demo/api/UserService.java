package com.switchvov.magicrpc.demo.api;

/**
 * @author switch
 * @since 2024/3/6
 */
public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    long getId(long id);

    long getId(User user);

    long getId(float id);

    int getId(int id);

    String getName();

    int[] getIds();

    long[] getLongIds();

    int[] getIds(int[] ids);
}
