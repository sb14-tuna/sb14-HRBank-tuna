package com.sb14.hrbank.domain.repository.file;

import com.sb14.hrbank.domain.entity.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
