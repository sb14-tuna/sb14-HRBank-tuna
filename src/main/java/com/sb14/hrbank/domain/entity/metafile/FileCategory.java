package com.sb14.hrbank.domain.entity.metafile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum FileCategory {
    BACKUP_CSV("BACKUP"), PROFILE_IMAGE("PIMG"), ERROR_LOG("ERROR");

    String field;
}
