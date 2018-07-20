package e.kevin.familyhistoryclient.Models;

/**
 * Holds the current color and value of the various settings drop downs and switches
 */
public class SettingModel {
    private String value;

    /**
     * Constructor for the setting. Requires that the value be set during creation
     * @param value Value to be assigned to this setting
     */
    SettingModel(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the current setting
     * @return Returns the value assigned to this setting
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the current setting
     * @param value value to be assigned to this setting
     */
    void setValue(String value) {
        this.value = value;
    }
}
