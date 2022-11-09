package src.main.java;

public class Beweging {

    private Coördinaat start;
    private Coördinaat eind;
    private int tijdstip;
    private int snelheidX;
    private int snelheidY;

    public Beweging(int tijdstip, Coördinaat start, Coördinaat eind, int snelheidX, int snelheidY) {
        this.tijdstip = tijdstip;
        this.start = start;
        this.eind = eind;
        this.snelheidX = snelheidX;
        this.snelheidY = snelheidY;
    }




}
