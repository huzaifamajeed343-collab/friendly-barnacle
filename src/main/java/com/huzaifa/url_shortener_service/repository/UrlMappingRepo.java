package com.huzaifa.url_shortener_service.repository;

import com.huzaifa.url_shortener_service.model.UrlMappingModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlMappingRepo extends JpaRepository<UrlMappingModel, Long> {
    boolean existsByShortCode(String shortCode);

    @Query(value = "SELECT * FROM url_mappings WHERE short_code = :code",nativeQuery = true)
    Optional<UrlMappingModel> findByShortCode(@Param("code") String code);

    //@Query(value = "SELECT * FROM url_mappings WHERE long_url = :url",nativeQuery = true)
    //optional<UrlMappingModel> findBy



}
