import com.changgou.util.IdWorker;
import org.springframework.cloud.commons.util.IdUtils;

/**
 * @ClassName IdTest
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月19日 15:01
 * @Version 1.0.0
*/
public class IdTest {

  public static void main(String[] args) {
    IdWorker idWorker = new IdWorker(1,1);
    long id = idWorker.nextId();
    System.out.println(id);
  }
}
