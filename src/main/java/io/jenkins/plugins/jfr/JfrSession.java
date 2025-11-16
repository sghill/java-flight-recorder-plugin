package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;
import java.util.Objects;

public class JfrSession implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    private final String name;

    @JsonProperty
    private final long id;

    public JfrSession(@NonNull String name, long id) {
        this.name = name;
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JfrSession that = (JfrSession) o;
        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return "JfrSession{" + "name='" + name + '\'' + ", id=" + id + '}';
    }
}
