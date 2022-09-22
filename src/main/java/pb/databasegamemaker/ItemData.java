package pb.databasegamemaker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.EntityManager;

public class ItemData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty useAction;
    private final SimpleStringProperty goAction;
    private final SimpleStringProperty grabable;
    private final SimpleStringProperty target;
    private final ItemEntity entity;

    public ItemData(String name, String useAction, String goAction, boolean grabable, String target) throws Exception{
        id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty(name);
        this.useAction = new SimpleStringProperty(useAction);
        this.goAction = new SimpleStringProperty(goAction);
        this.grabable = new SimpleStringProperty(grabable ? "true" : "false");
        this.target = new SimpleStringProperty(target);
        entity = new ItemEntity(this);
    }

    public ItemData(ItemEntity entity){
        id = new SimpleIntegerProperty(entity.getId());
        name = new SimpleStringProperty(entity.getName());
        useAction = new SimpleStringProperty(entity.getOn_use() != null ? String.valueOf(entity.getOn_use().getId()) : "");
        goAction = new SimpleStringProperty(entity.getOn_go() != null ? String.valueOf(entity.getOn_go().getId()) : "");
        grabable = new SimpleStringProperty(entity.isGrabable() ? "true" : "false");
        target = new SimpleStringProperty(entity.getUse_target() != null ? String.valueOf(entity.getUse_target().getId()) : "");
        this.entity = entity;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
        entity.setName(name);
    }

    public String getUseAction() {
        return useAction.get();
    }

    public SimpleStringProperty useActionProperty() {
        return useAction;
    }

    public void setUseAction(String useAction) throws Exception {
        this.useAction.set(useAction);

        if(useAction.equals("")){
            entity.setOn_use(null);
        }else {
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            try {
                entity.setOn_use(em.find(ActionEntity.class, Integer.parseInt(useAction)));
                if(entity.getOn_use() == null) throw new Exception("Action with id: " + useAction + " does not exist");
            } catch (Exception ignored) {
                entity.setOn_use(null);
                throw new Exception("Action with id: " + useAction + " does not exist");
            } finally {
                em.close();
            }
        }
    }

    public String getGoAction() {
        return goAction.get();
    }

    public SimpleStringProperty goActionProperty() {
        return goAction;
    }

    public void setGoAction(String goAction) throws Exception {
        this.goAction.set(goAction);

        if(goAction.equals("")){
            entity.setOn_go(null);
        }else {
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            try {
                entity.setOn_go(em.find(ActionEntity.class, Integer.parseInt(goAction)));
                if(entity.getOn_go() == null) throw new Exception("Action with id: " + goAction + " does not exist");
            } catch (Exception ignored) {
                entity.setOn_go(null);
                throw new Exception("Action with id: " + goAction + " does not exist");
            } finally {
                em.close();
            }
        }
    }

    public String getGrabable() {
        return grabable.get();
    }

    public SimpleStringProperty grabableProperty() {
        return grabable;
    }

    public void setGrabable(String grabable) {
        this.grabable.set(grabable);
        entity.setGrabable(grabable.equalsIgnoreCase("true"));
    }

    public String getTarget() {
        return target.get();
    }

    public SimpleStringProperty targetProperty() {
        return target;
    }

    public void setTarget(String target) throws Exception {
        this.target.set(target);

        if(target.equals("")){
            entity.setUse_target(null);
        }else {
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            try {
                entity.setUse_target(em.find(ItemEntity.class, Integer.parseInt(target)));
                if(entity.getUse_target() == null) throw new Exception("Item with id: " + target + " does not exist");
            } catch (Exception ignored) {
                entity.setUse_target(null);
                throw new Exception("Item with id: " + target + " does not exist");
            } finally {
                em.close();
            }
        }
    }

    public ItemEntity getEntity() {
        return entity;
    }
}
