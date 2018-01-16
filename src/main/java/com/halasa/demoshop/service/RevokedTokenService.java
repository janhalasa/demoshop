package com.halasa.demoshop.service;

import com.halasa.demoshop.service.domain.RevokedToken;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import com.halasa.demoshop.service.repository.RevokedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final GenericWriteOnlyRepository genericWriteOnlyRepository;

    @Autowired
    public RevokedTokenService(
            RevokedTokenRepository revokedTokenRepository,
            GenericWriteOnlyRepository genericWriteOnlyRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.genericWriteOnlyRepository = genericWriteOnlyRepository;
    }

    @Transactional
    public RevokedToken revoke(String jwtString) {
        final RevokedToken revokedToken = new RevokedToken(this.hash(jwtString));
        return this.genericWriteOnlyRepository.save(revokedToken);
    }

    public boolean isRevoked(String jwtString) {
        try {
            this.revokedTokenRepository.getByPk(hash(jwtString));
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private String hash(String jwtString) {
        return DigestUtils.md5DigestAsHex(jwtString.getBytes(StandardCharsets.UTF_8));
    }
}
