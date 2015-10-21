package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 10/17/15.
 */
public class LogEntryProductivity {

    public static final String TAG = "LogEntryProductivity";

    private long id;
    private long hive;
    private String visitDate;
    private String honeyAddSupers;
    private String honeyRemoveSupers;
    private float extractedHoney;
    private int addPollenTrap;
    private int removePollenTrap;
    private float pollenCollected;
    private float beeswaxCollected;

    public int getAddPollenTrap() {
        return addPollenTrap;
    }

    public void setAddPollenTrap(int addPollenTrap) {
        this.addPollenTrap = addPollenTrap;
    }

    public float getBeeswaxCollected() {
        return beeswaxCollected;
    }

    public void setBeeswaxCollected(float beeswaxCollected) {
        this.beeswaxCollected = beeswaxCollected;
    }

    public float getExtractedHoney() {
        return extractedHoney;
    }

    public void setExtractedHoney(float extractedHoney) {
        this.extractedHoney = extractedHoney;
    }

    public long getHive() {
        return hive;
    }

    public void setHive(long hive) {
        this.hive = hive;
    }

    public String getHoneyAddSupers() {
        return honeyAddSupers;
    }

    public void setHoneyAddSupers(String honeyAddSupers) {
        this.honeyAddSupers = honeyAddSupers;
    }

    public float getPollenCollected() {
        return pollenCollected;
    }

    public void setPollenCollected(float pollenCollected) {
        this.pollenCollected = pollenCollected;
    }

    public String getHoneyRemoveSupers() {
        return honeyRemoveSupers;
    }

    public void setHoneyRemoveSupers(String honeyRemoveSupers) {
        this.honeyRemoveSupers = honeyRemoveSupers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRemovePollenTrap() {
        return removePollenTrap;
    }

    public void setRemovePollenTrap(int removePollenTrap) {
        this.removePollenTrap = removePollenTrap;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
