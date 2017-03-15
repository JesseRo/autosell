package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taobao.autosell.entity.UserInfo;

/**
 * Created by asus on 2016/11/20.
 */
public interface UserInfoRepository extends JpaRepository<UserInfo,String > {
}
