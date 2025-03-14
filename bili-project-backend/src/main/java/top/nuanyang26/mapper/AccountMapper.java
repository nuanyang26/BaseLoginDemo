package top.nuanyang26.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.nuanyang26.entity.dto.Account;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
