import java.util.*;
public class Util {
    public static int toHashCode(Object object) {
        return object == null ? 0 : object.hashCode();
    }
    public static <K, V> HashMap<K, V> copyHashMap(HashMap<K, V> map) {
        HashMap<K, V> copy = new HashMap<>();
        for (K key : map.keySet()) {
            copy.put(key, map.get(key));
        }
        return copy;
    } 
}