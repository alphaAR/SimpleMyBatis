package Cache;

public class CacheKey {
    private static final int DEFAULT_HASHCODE = 17;
    private static final int DEFAULT_MULTIPLIER = 37;

    private int hashCode;
    private int count;
    private int multiplier;

    public CacheKey() {
        this.hashCode = DEFAULT_HASHCODE;
        this.count = 0;
        this.multiplier = DEFAULT_MULTIPLIER;
    }

    public int getCode() {
        return hashCode;
    }

    public void update(Object object){
        int baseHashCode = object==null ? 1 : object.hashCode();
        count++;
        baseHashCode *= count;
        hashCode = multiplier * hashCode + baseHashCode;
    }
}
