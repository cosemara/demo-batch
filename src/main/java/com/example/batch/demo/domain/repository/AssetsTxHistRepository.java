package com.example.batch.demo.domain.repository;

import com.example.batch.demo.domain.entity.AssetsTxHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AssetsTxHistRepository extends JpaRepository<AssetsTxHistory, Long> {
}
