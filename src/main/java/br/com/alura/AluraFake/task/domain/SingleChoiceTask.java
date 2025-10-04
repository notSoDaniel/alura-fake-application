package br.com.alura.AluraFake.task.domain;

import br.com.alura.AluraFake.task.Type;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    public SingleChoiceTask() {
        super();
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public Type getType(){
        return Type.SINGLE_CHOICE;
    }
}