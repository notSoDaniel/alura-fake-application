package br.com.alura.AluraFake.task.domain;

import br.com.alura.AluraFake.task.Type;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {

    public OpenTextTask() {
        super();
    }

    @Override
    public Type getType() {
        return Type.OPEN_TEXT;
    }
}