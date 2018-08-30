package appLogic;

import java.util.List;

/**
 * BusInfoList takes general station board return data list and
 * saves it as a Java object
 */
public class BusInfoList {

    private List<BusInfo> stationboard;

    public List<BusInfo> getsStaionboard() {
        return stationboard;
    }

    public void setStationboard(List<BusInfo> stationboard) {
        this.stationboard = stationboard;
    }
}
