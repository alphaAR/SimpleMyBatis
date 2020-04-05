package executors;

public class CachingExecutor {
    /*实现二级缓存功能*/
    private Executor executor;

    public CachingExecutor(Executor executor) {
        this.executor = executor;
    }
}
