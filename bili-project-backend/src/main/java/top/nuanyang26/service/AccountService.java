package top.nuanyang26.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;
import top.nuanyang26.entity.dto.Account;
import top.nuanyang26.entity.vo.request.ConfirmResetVO;
import top.nuanyang26.entity.vo.request.EmailRegisterVo;
import top.nuanyang26.entity.vo.request.EmailResetVO;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    //    type表示验证码的目的 如注册，重置密码。方便在消费时发送不同的文案
//    通过ip地址限制请求频率
    String registerEmailVerifyCode(String type, String email, String ip);

    String registerEmailAccount(EmailRegisterVo vo);

    String resetConfirm(ConfirmResetVO vo);

    String resetEmailAccountPassword(EmailResetVO vo);
}
