package com.sb14.hrbank.domain.entity.metafile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "files")
@Getter
public class MetaFile {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_seq")
    @SequenceGenerator(
        name = "files_seq",
        sequenceName = "files_seq",
        allocationSize = 1
    )
    @Column(name = "file_id")
    Long id;

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "file_size", nullable = false)
    Long fileSize;

    @Column(name = "file_type", nullable = false)
    String fileType;

    @Column(name = "file_category", nullable = false)
    @Enumerated(EnumType.STRING)
    FileCategory category;          // todo 파일타입과 카테고리가 겹쳐보이지만 실제 저장 형식은 파일타입이 관리하고 후에 필터링을 할 때 MIME 타입으로 하면 코드가 가독성이 떨어질거라 생각되어 이넘타입을 정의하고 필터링은 이넘으로 분기하도록 설곔

    @Column(name = "file_path", nullable = false)
    String filePath;

    public static MetaFile init(String name, Long size, String type, FileCategory category, String path){
        return MetaFile.builder().fileName(name).fileSize(size).fileType(type).category(category).filePath(path).build();
    }
}
