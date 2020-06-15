package com.sfwl.bh.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sfwl.bh.utils.DateUtil;

import java.util.Date;

public class JWTUtil {

    private static final String SECRET = "MtSSbjq2ib0CLvSuwsEatOhNMRU9rMt9L7SbuCIuzIfm7RWfmChygKzEZ4xCjsJYtqrQ7oky9istL1qjaNtN76J43EJtt8OFHcMRQQoayfVpVuyJz7vGbDc25uwIBn2c";

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getUserIdString(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String sign(String userIdString) {
        Date date = DateUtil.addDays(30);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create().withClaim("userId", userIdString).withExpiresAt(date).sign(algorithm);
    }

    public static void main(String[] args) {
        System.out.println(sign("1"));
    }
}
