package org.megadix.jfcm.utils;

import org.megadix.jfcm.CognitiveMap;

/**
 * Basic implementation of {@link org.megadix.jfcm.utils.FcmRunner}
 */
public abstract class BaseFcmRunner implements FcmRunner {

    protected CognitiveMap map;
    protected int maxEpochs;

    public BaseFcmRunner() {
    }

    public BaseFcmRunner(int maxEpochs) {
        this.maxEpochs = maxEpochs;
    }

    public BaseFcmRunner(CognitiveMap map, int maxEpochs) {
        this.map = map;
        this.maxEpochs = maxEpochs;
    }

    public CognitiveMap getMap() {
        return map;
    }

    public void setMap(CognitiveMap map) {
        this.map = map;
    }

    public void setMaxEpochs(int maxEpochs) {
        this.maxEpochs = maxEpochs;
    }
}
