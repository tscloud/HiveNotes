package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Hive {

    public static final String TAG = "Hive";

    private long id;
    private long apiary;
    private String name;
    private String species;
    private String foundationType;


    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public String getFoundationType() {
        return foundationType;
    }

    public void setFoundationType(String foundationType) {
        this.foundationType = foundationType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
