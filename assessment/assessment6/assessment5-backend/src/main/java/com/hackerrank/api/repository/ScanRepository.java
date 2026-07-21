package com.hackerrank.api.repository;

import com.hackerrank.api.model.Scan;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, Long> {

	List<Scan> findByDomainName(String domainName, Sort by);

	
}
