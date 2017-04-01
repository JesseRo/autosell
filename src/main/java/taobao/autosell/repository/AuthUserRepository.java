package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import taobao.autosell.entity.AuthUser;

/**
 * Created by Administrator on 2017/3/30.
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser,String>{
}
