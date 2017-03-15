package taobao.autosell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by asus on 2017/2/5.
 */
@Data
@Entity
@Table(name = "autosell_black_list")
public class BlackList {
    @Id
    private String id;
}
