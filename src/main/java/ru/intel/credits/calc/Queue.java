package ru.intel.credits.calc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class Queue {

    private List<Long> credsId;
    static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public synchronized List<Long> getSubList() {
        try {
            log.debug("getSubList start " + credsId.size());
            int subSize = 1000;
            List result;
            try {
                if (credsId.size() >= subSize) {
                    result = credsId.subList(0, subSize);
                    credsId = List.copyOf(credsId.subList(subSize, credsId.size()));
                } else {
                    if (credsId.size() > 0) {
                        result = credsId.subList(0, credsId.size());
                        credsId = List.of();
                    } else {
                        log.debug("getSubList finish = " + credsId.size());
                        return List.of();
                    }
                }
                log.debug("getSubList semi-finish = " + credsId.size());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("getSubList finish " + credsId.size());
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("getSubList in last catch"); return List.of();
    }
}
