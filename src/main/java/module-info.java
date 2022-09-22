module pb.databasegamemaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.persistence;
    requires hibernate.entitymanager;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;

    opens pb.databasegamemaker to javafx.fxml, org.hibernate.orm.core;
    exports pb.databasegamemaker;
}