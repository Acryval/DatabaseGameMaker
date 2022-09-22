package pb.databasegamemaker;

import javax.persistence.*;

@Entity
@Table(name = "data_items", schema = "pb")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "on_use")
    private ActionEntity on_use;
    @ManyToOne
    @JoinColumn(name = "on_go")
    private ActionEntity on_go;
    private boolean grabable;
    @ManyToOne
    @JoinColumn(name = "use_target")
    private ItemEntity use_target;

    public ItemEntity() {}
    public ItemEntity(ItemData data) throws Exception {
        name = data.getName();
        grabable = data.getGrabable().equalsIgnoreCase("true");

        int uai = Integer.parseInt(data.getUseAction());
        int gai = Integer.parseInt(data.getGoAction());
        int uti = Integer.parseInt(data.getTarget());

        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        try {on_use = em.find(ActionEntity.class, uai);}catch (Exception ignored){throw new Exception("Action with id: " + uai + " does not exist");}
        try {on_go = em.find(ActionEntity.class, gai);}catch (Exception ignored){throw new Exception("Action with id: " + gai + " does not exist");}
        try {use_target = em.find(ItemEntity.class, uti);}catch (Exception ignored){throw new Exception("Item with id: " + uti + " does not exist");}
        em.close();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActionEntity getOn_use() {
        return on_use;
    }

    public void setOn_use(ActionEntity on_use) {
        this.on_use = on_use;
    }

    public ActionEntity getOn_go() {
        return on_go;
    }

    public void setOn_go(ActionEntity on_go) {
        this.on_go = on_go;
    }

    public boolean isGrabable() {
        return grabable;
    }

    public void setGrabable(boolean grabable) {
        this.grabable = grabable;
    }

    public ItemEntity getUse_target() {
        return use_target;
    }

    public void setUse_target(ItemEntity use_target) {
        this.use_target = use_target;
    }
}
