package org.onosproject.hcp.protocol;

/**
 * @Author ldy
 * @Date: 20-2-13 下午7:14
 * @Version 1.0
 */
public enum HCPHostState {
    ACTIVE(true),
    INACTIVE(false);

    private final boolean activeState;

    private HCPHostState(boolean activeState){
        this.activeState=activeState;
    }

    public boolean isActive(){
        return activeState;
    }
}
