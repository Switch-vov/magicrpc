package com.switchvov.magicrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    List<String> data;
}
