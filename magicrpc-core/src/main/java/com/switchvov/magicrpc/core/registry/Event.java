package com.switchvov.magicrpc.core.registry;

import com.switchvov.magicrpc.core.meta.InstanceMeta;
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
    List<InstanceMeta> data;
}
