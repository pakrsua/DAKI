package com.daki.api.request;

import com.daki.db.entity.Skin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@ApiModel("LoginRequest")
public class UserLoginReq {
    @ApiModelProperty(name="이메일(=ID)")
    String email;

    @ApiModelProperty(name="비밀번호")
    String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
