package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFileRepository extends JpaRepository<MetaFile, Long> {
}
