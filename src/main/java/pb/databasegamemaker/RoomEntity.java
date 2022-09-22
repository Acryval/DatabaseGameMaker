package pb.databasegamemaker;

import javax.persistence.*;

@Entity
@Table(name = "data_rooms", schema = "pb")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int sides;

    public RoomEntity() {}

    public RoomEntity(RoomData data) {
        this.name = data.getName();
        this.sides = Integer.parseInt(data.getSides());
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

    public int getSides() {
        return sides;
    }

    public void setSides(int sides) {
        this.sides = sides;
    }
}
