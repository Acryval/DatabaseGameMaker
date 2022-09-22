package pb.databasegamemaker;

import javax.persistence.*;

@Entity
@Table(name = "data_item_locations", schema = "pb")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private ItemEntity item;
    @ManyToOne
    private RoomEntity room;
    private int side;

    public LocationEntity() {}
    public LocationEntity(LocationData data) throws Exception{
        int iid = Integer.parseInt(data.getItem());
        int rid = Integer.parseInt(data.getRoom());

        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        try {item = em.find(ItemEntity.class, iid);}catch (Exception ignored){throw new Exception("Item with id: " + iid + " does not exist");}
        try {room = em.find(RoomEntity.class, rid);}catch (Exception ignored){throw new Exception("Room with id: " + rid + " does not exist");}
        em.close();

        side = Integer.parseInt(data.getSide());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
