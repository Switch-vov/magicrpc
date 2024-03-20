package com.switchvov.magicrpc.core.registry;

/**
 * @author switch
 * @since 2024/3/17
 */
public interface ChangeListener {
    /**
     *
     * @param event
     */
    void fire(Event event);
}
