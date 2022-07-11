package com.example.democamel.repo;

import com.example.democamel.model.RegionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionReportRepository extends JpaRepository<RegionReport, Long> {
}
