package slidenerd.vivz.fpam.prefs;

@SharedPref
public interface MyPrefs {

        // The field name will have default value "John"
    @DefaultString("John")
    String name();

        // The field age will have default value 42
    @DefaultInt(42)
    int age();

        // The field lastUpdated will have default value 0
    long lastUpdated();

}