package appLogic;

public class BusDTO
{
    private String busStop;
    private String busNumber;
    private String bBstation;
    private String color;

    public BusDTO(String busStop, String busNumber, String bBstation, String color)
    {
        this.busStop = busStop;
        this.busNumber = busNumber;
        this.bBstation = bBstation;
        this.color = color;
    }

    public String getBusStop()
    {
        return busStop;
    }

    public void setBusStop(String busStop)
    {
        this.busStop = busStop;
    }

    public String getBusNumber()
    {
        return busNumber;
    }

    public void setBusNumber(String busNumber)
    {
        this.busNumber = busNumber;
    }

    public String getbBstation()
    {
        return bBstation;
    }

    public void setbBstation(String bBstation)
    {
        this.bBstation = bBstation;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    @Override
    public String toString()
    {
        return "BusDTO{" +
                "busStop='" + busStop + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", bBstation='" + bBstation + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
