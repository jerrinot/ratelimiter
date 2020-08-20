package info.jerrinot;

import com.hazelcast.client.HazelcastClient;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import org.junit.Test;

import javax.cache.Cache;
import java.time.Duration;

public final class BucketTest {
    public static final String CACHE_NAME = "myCache";
    public static final String IP = "192.168.1.1";

    private static final BucketConfiguration BUCKET_CONFIGURATION = Bucket4j.configurationBuilder()
            .addLimit(Bandwidth.simple(5, Duration.ofSeconds(1)))
            .build();

    @Test
    public void notReallyATest() throws Exception {
        var hz = HazelcastClient.newHazelcastClient();
        Cache<String, GridBucketState> cache = hz.getCacheManager().getCache(CACHE_NAME);
        ProxyManager<String> buckets = Bucket4j.extension(JCache.class).proxyManagerForCache(cache);

        Bucket bucket = buckets.getProxy(IP, BUCKET_CONFIGURATION);
        for (;;) {
            if (bucket.tryConsume(1)) {
                System.out.println('.');
            } else {
                System.out.println("X");
            }
            Thread.sleep(100);
        }
    }
}
