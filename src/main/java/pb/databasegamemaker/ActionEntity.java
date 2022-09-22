package pb.databasegamemaker;

import javax.persistence.*;

@Entity
@Table(name = "data_actions", schema = "pb")
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String action;

    public ActionEntity() {}

    public ActionEntity(ActionData data){
        action = data.getAction();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
