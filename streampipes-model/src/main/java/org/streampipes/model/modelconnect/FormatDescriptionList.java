package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:FormatDescriptionList")
@Entity
public class FormatDescriptionList extends NamedStreamPipesEntity {


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<FormatDescription> list;

    public FormatDescriptionList() {
        super("http://bla.de#2", "", "");
        list = new ArrayList<>();
    }


    public void addDesctiption(FormatDescription formatDescription) {
        list.add(formatDescription);
    }

    public FormatDescriptionList(List<FormatDescription> list) {
        this.list = list;
    }

    public List<FormatDescription> getList() {
        return list;
    }

    public void setList(List<FormatDescription> list) {
        this.list = list;
    }
}
