package com.daki.api.service;

import com.daki.api.request.*;
import com.daki.api.response.CheckRes;
import com.daki.api.response.UserLoginRes;
import com.daki.common.config.TokenDto;
import com.daki.common.config.TokenProvider;
import com.daki.common.util.RefreshToken;
import com.daki.common.util.RefreshTokenRepository;
import com.daki.common.util.SecurityUtil;
import com.daki.db.entity.*;
import com.daki.db.repository.DollRepository;
import com.daki.db.repository.ItemRepository;
import com.daki.db.repository.UserItemRepository;
import com.daki.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DollRepository dollRepository;

    @Autowired
    UserItemRepository userItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @Transactional
    @Override
    public User createUser(UserJoinReq userJoinReq){
        User user = new User(userJoinReq.getEmail(), userJoinReq.getNickName(), passwordEncoder.encode(userJoinReq.getPassword()),
                userJoinReq.getBirth(), userJoinReq.getGender(), 0, Authority.valueOf("ROLE_USER"));

        User saveUser = userRepository.save(user);  //???????????? ??????

        Doll doll = new Doll(0, saveUser, userJoinReq.getDollType());
        dollRepository.save(doll);

        List<Item> itemList = itemRepository.findAll();

        for (Item item:itemList) {
            UserItem userItem;

            if(item.getItemNo()==1){
                userItem = new UserItem(1,doll,item);
            }else{
                userItem = new UserItem(0, doll, item);
            }
            userItemRepository.save(userItem);
        }

        return saveUser;
    }

    @Override
    public User getUserByUserId(String email) {
        User user = userRepository.getUserByUserEmail(email);
        return user;
    }

    @Transactional
    @Override
    public ResponseEntity loginUser(UserLoginReq userLoginReq) {
        // 1. Login ID/PW ??? ???????????? AuthenticationToken ??????
        UsernamePasswordAuthenticationToken authenticationToken = userLoginReq.toAuthentication();
        System.out.println(authenticationToken.toString());
        // 2. ????????? ?????? (????????? ???????????? ??????) ??? ??????????????? ??????
        // authenticate ???????????? ????????? ??? ??? CustomUserDetailsService ?????? ???????????? loadUserByUsername ???????????? ?????????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken ??????
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenKey(authentication.getName())
                .tokenValue(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
//        tokenDto.getAccessToken()
        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("Refresh_Authorization", tokenDto.getRefreshToken());

        CheckRes checkRes = new CheckRes();
        checkRes.setStatusText("Login Success");



        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(tokenDto);
    }

    @Override
    public Boolean checkEmail(String email) {
        boolean findCheck = userRepository.existsByUserEmail(email);
        return findCheck;
    }

    @Override
    public Boolean checkNickName(String nickName) {
        boolean findCheck = userRepository.existsByUserNickname(nickName);
        return findCheck;
    }

    @Override
    public Boolean checkPassword(UserPasswordReq passwordReq) {


        User user = userRepository.findByUserEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                -> new RuntimeException("????????? ??????"));
//        System.out.println("?????? ???????????? : " + user.getUserPassword());
//        System.out.println("?????? ???????????? : " + passwordReq.getMemberPassword() + " => ????????? : "+passwordEncoder.encode(passwordReq.getMemberPassword()));
        return passwordEncoder.matches(passwordReq.getMemberPassword(), user.getUserPassword());

    }


    @Transactional
    @Override
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token ??????
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token ??? ???????????? ????????????.");
        }

        System.out.println("================Enter Reissue====================");
        System.out.println("access Token : " + tokenRequestDto.getAccessToken());
        // 2. Access Token ?????? Member ID ????????????
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. ??????????????? Member ID ??? ???????????? Refresh Token ??? ?????????
        RefreshToken refreshToken = refreshTokenRepository.findByTokenKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("???????????? ??? ??????????????????."));

        // 4. Refresh Token ??????????????? ??????
        if (!refreshToken.getTokenValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("????????? ?????? ????????? ???????????? ????????????.");
        }

        // 5. ????????? ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. ????????? ?????? ????????????
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // ?????? ??????
        return tokenDto;
    }

    @Override
    public User getMyInfo() {
//        String str = SecurityUtil.getCurrentUserEmail();
//        System.out.println(str);
        User user = userRepository.findByUserEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                -> new RuntimeException("????????? ??????"));

        System.out.println("User : "+user.toString());
        return user;
    }

    @Transactional
    @Override
    public void modify(UserModifyReq modifyReq) {
        User user = userRepository.findByUserEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                -> new RuntimeException("????????? ??????"));
        user.modify(passwordEncoder.encode(modifyReq.getPassword()), modifyReq.getNickName());

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void remove() {
        User user = userRepository.findByUserEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                -> new RuntimeException("????????? ??????"));
        userRepository.delete(user);
    }

    @Override
    public ResponseEntity tokenEnter(HttpServletRequest request, Object body, int status) {

        String refresh = request.getHeader("Refresh_Authorization");
        if (!tokenProvider.validateToken(refresh)) {
            throw new RuntimeException("Refresh Token ??? ???????????? ????????????.");
        }

        StringTokenizer st = new StringTokenizer(request.getHeader("Authorization"));

        String accessTop = st.nextToken();
        String accessBody = st.nextToken();
        System.out.println("TOP : " +accessTop);
        System.out.println("BODY : " +accessBody);
        // 2. Access Token ?????? Member ID ????????????
        Authentication authentication = tokenProvider.getAuthentication(accessBody);

        // 3. ??????????????? Member ID ??? ???????????? Refresh Token ??? ?????????
        RefreshToken refreshToken = refreshTokenRepository.findByTokenKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("???????????? ??? ??????????????????."));

        // 4. Refresh Token ??????????????? ??????
        if (!refreshToken.getTokenValue().equals(refresh)) {
            throw new RuntimeException("????????? ?????? ????????? ???????????? ????????????.");
        }

        // 5. ????????? ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. ????????? ?????? ????????????
//        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
//        refreshTokenRepository.save(newRefreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("Refresh_Authorization", refresh);

        return ResponseEntity.status(status)
                .headers(httpHeaders)
                .body(body);
    }
}
