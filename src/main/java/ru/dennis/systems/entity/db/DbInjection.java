package ru.dennis.systems.entity.db;


import com.fasterxml.jackson.annotation.JsonProperty;
import ru.dennis.systems.entity.DefaultEntity;
import lombok.Data;
import lombok.ToString;


import javax.persistence.*;

@Data

@Entity (name = "db_injection")
@ToString
public class DbInjection implements DefaultEntity {

    @Transient
    public boolean root;
    @Id
    @GeneratedValue (generator = "db_injection_seq")
    private Long id;

    private String sql;

    private String name;

    @JsonProperty ("always_to_run")
    private boolean alwaysToRun;

    @JsonProperty ("fail_on_execute")
    private boolean failOnExecute;

    @JsonProperty ("restart_on_fail")
    private boolean restartOnFail = false;

    private String script;

    @Column(name = "if_sql")
    @JsonProperty ("if-not-sql")
    private String ifSql;

    @Column(name = "db")
    @JsonProperty ("db")
    private String db;

    private boolean result;

    private String message;

    private String profile;

    @Transient
    private boolean isolated;

    @Override
    public boolean equals(Object o){
        if (getName() == null){
            return false;
        }
        if (o instanceof String){
            return getName().equals(o);
        } else if (o instanceof DbInjection){
            return getName() != null &&this.getName().equals(((DbInjection) o).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getName() == null ? 0 : getName().hashCode();
    }
}
