package taobao.autosell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by asus on 2016/11/20.
 */
@Data
@Entity
@Table(name = "autosell_user")
public class UserInfo {
    @Id
    private String name;
    private String link;
}
