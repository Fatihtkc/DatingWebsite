package com.datingapp.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datingapp.backend.service.OtpHashService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class OtpHashServiceImpl implements OtpHashService {

    private final byte[] secret;

    public OtpHashServiceImpl(@Value("${app.security.otp-secret}") String secret){
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String hash(String email, String otp, String purpose){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(secret, "HmacSHA256");

            mac.init(key);

            String data = email + ":" + purpose + ":" + otp;

            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(result);

        } catch (Exception e) {
            throw new IllegalStateException("Could not hash OTP", e);
        }
    }
}