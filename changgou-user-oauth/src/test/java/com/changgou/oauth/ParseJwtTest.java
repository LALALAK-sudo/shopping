package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {

    @Test
    public void parseJwt() {
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYxODMyMDcwNywiYXV0aG9yaXRpZXMiOlsidXNlciIsImFjY291bnQiLCJzYWxlc21hbiJdLCJqdGkiOiI0NTQwOTBlMS00ZmIwLTQwMGMtOGYyNi1lYmU1ZTg5YzVjNDAiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiaXRoZWltYSJ9.gD3gRqLMtGmm-YMfAB2DT-YaBm5b0CR8c3zCHPeyP4j8acbd9TGHOrVIj1gjQZrQZubb_yEcyt6340BqJ746yavB2ebAvITi7rKdOt7qpSeGtQpxQ452rdxjjfvHDswMuDZ_GJUGnd8DQsOL8GefuT0pTVQfWgRF0mQWT52J4_wqWXLoqbRzYr9H1ipwjU4mekFjmzXUzPL0758zNSONI4HyU-dMhYTbQgRzIeYC1STtu7vgg-Eqvg9ic4yIwrPubR8ce9SRYIv8nIobSh3RpwImCdPjEozcAu484bE2cogjvCiN1eObu7Phd8vumSwNhjEc7mp3UTj2RIUIGyfM_A";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";
        Jwt token = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        String claims = token.getClaims();
        System.out.println(claims);
    }
}
