package ru.dennis.systems.db;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DbUpdateConfig {
    @Value("${global.use_db_updater:false}")
    private boolean useDbUpdater;

    @Value("${global.project_update_file:null}")
    private String projectUpdateFile;

    @Value("${global.basic_update_file:db/root-sql-init/db.yml}")
    private String defaultUpdateFile;

}
