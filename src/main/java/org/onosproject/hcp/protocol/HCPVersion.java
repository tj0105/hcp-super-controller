package org.onosproject.hcp.protocol;

public enum HCPVersion {
    HCP_10(1);

    public final int wireVersion;

    HCPVersion(int wireVersion){
        this.wireVersion=wireVersion;
    }

    public int getWireVersion(){
        return wireVersion;
    }

    public static HCPVersion ofWireValue(int wireValue){
        switch(wireValue){
            case 1:
                return HCP_10;
            default:
                return null;
        }
    }
}
