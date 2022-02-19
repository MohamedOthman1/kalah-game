package com.kalah.backend.dal;

import com.kalah.backend.models.KalahPit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPitDAO extends JpaRepository<KalahPit, Long> {
    
}
