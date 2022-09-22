package pb.databasegamemaker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class LoaderStage implements Initializable {
    public static final String
            SQL_DROP_SCHEMA_PB = "DROP SCHEMA IF EXISTS pb CASCADE;",
            SQL_CREATE_HISTORY_TRIGGER = """
                    CREATE TRIGGER history_insert_trigger
                        AFTER INSERT
                        ON pb.history
                        FOR EACH ROW
                        EXECUTE FUNCTION pb.history_trigger();""",
            SQL_SCHEMA_FIND = "SELECT COUNT(schema_name) FROM information_schema.schemata WHERE schema_name LIKE 'pb';",
            SQL_SCHEMA_TABLES_COUNT = "SELECT COUNT(table_name) FROM information_schema.tables WHERE table_schema LIKE 'pb' AND table_type LIKE 'BASE TABLE';",
            SQL_CREATE_SCHEMA_PB = "CREATE SCHEMA IF NOT EXISTS pb\n" +
                    "    AUTHORIZATION current_user;",
            SQL_CREATE_HISTORY_TRIGGER_FUNC = """
                    CREATE OR REPLACE FUNCTION pb.history_trigger()
                        RETURNS trigger
                        LANGUAGE 'plpgsql'
                        COST 100
                        VOLATILE NOT LEAKPROOF
                    AS $BODY$
                    declare
                        v_arg character varying[];
                        func pb.data_functions%rowtype;
                    begin
                        if not new.auto_generated then
                            v_arg = regexp_split_to_array(new.message, '\\s+');
                            select * into func from pb.data_functions
                                where key = upper(v_arg[1]);
                     \s
                            if func is NULL then
                                call pb.say('Command ' || v_arg[1] || ' not found.');
                            else
                                execute func.value using v_arg;
                            end if;
                        end if;
                        return new;
                    end
                    $BODY$;

                    ALTER FUNCTION pb.history_trigger()
                        OWNER TO current_user;""",
            SQL_CREATE_TABLE_HISTORY = """
                    CREATE TABLE IF NOT EXISTS pb.history
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        auto_generated boolean NOT NULL DEFAULT true,
                        target_user character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        message character varying(1024) COLLATE pg_catalog."default" NOT NULL,
                        CONSTRAINT history_pkey PRIMARY KEY (id)
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.history
                        OWNER to current_user;
                    """,
            SQL_CREATE_TABLE_ACTIONS = """
                    CREATE TABLE IF NOT EXISTS pb.data_actions
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        action character varying(1024) COLLATE pg_catalog."default" NOT NULL,
                        CONSTRAINT data_actions_pkey PRIMARY KEY (id)
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.data_actions
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ROOMS = """
                    CREATE TABLE IF NOT EXISTS pb.data_rooms
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        name character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        sides integer NOT NULL DEFAULT 4,
                        CONSTRAINT data_rooms_pkey PRIMARY KEY (id),
                        CONSTRAINT name_uniq UNIQUE (name)
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.data_rooms
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ITEMS = """
                    CREATE TABLE IF NOT EXISTS pb.data_items
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        name character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        on_use integer,
                        on_go integer,
                        grabable boolean NOT NULL DEFAULT true,
                        use_target integer,
                        CONSTRAINT data_items_pkey PRIMARY KEY (id),
                        CONSTRAINT item_name_uniq UNIQUE (name),
                        CONSTRAINT on_go_fkey FOREIGN KEY (on_go)
                            REFERENCES pb.data_actions (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                            NOT VALID,
                        CONSTRAINT on_use_fkey FOREIGN KEY (on_use)
                            REFERENCES pb.data_actions (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                            NOT VALID,
                        CONSTRAINT use_target_fkey FOREIGN KEY (use_target)
                            REFERENCES pb.data_items (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                            NOT VALID
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.data_items
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ITEM_LOCATIONS = """
                    CREATE TABLE IF NOT EXISTS pb.data_item_locations
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        item_id integer NOT NULL,
                        room_id integer NOT NULL,
                        side integer NOT NULL,
                        CONSTRAINT data_item_locations_pkey PRIMARY KEY (id),
                        CONSTRAINT item_id_fkey FOREIGN KEY (item_id)
                            REFERENCES pb.data_items (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
                        CONSTRAINT room_id_fkey FOREIGN KEY (room_id)
                            REFERENCES pb.data_rooms (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.data_item_locations
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_FUNCTIONS = """
                    CREATE TABLE IF NOT EXISTS pb.data_functions
                    (
                        key character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        value character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        CONSTRAINT data_functions_pkey PRIMARY KEY (key)
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.data_functions
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ACTIVE_USERS = """
                    CREATE TABLE IF NOT EXISTS pb.active_users
                    (
                        username character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        location integer NOT NULL,
                        facing integer NOT NULL,
                        CONSTRAINT active_users_pkey PRIMARY KEY (username),
                        CONSTRAINT user_location_fkey FOREIGN KEY (location)
                            REFERENCES pb.data_rooms (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.active_users
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ACTIVE_INVENTORY = """
                    CREATE TABLE IF NOT EXISTS pb.active_inventory
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        target_user character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        item_id integer NOT NULL,
                        CONSTRAINT active_inventory_pkey PRIMARY KEY (id),
                        CONSTRAINT inv_item_fkey FOREIGN KEY (item_id)
                            REFERENCES pb.data_items (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
                        CONSTRAINT inv_user_fkey FOREIGN KEY (target_user)
                            REFERENCES pb.active_users (username) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.active_inventory
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_ACTIVE_ITEM_LOCATIONS = """
                    CREATE TABLE IF NOT EXISTS pb.active_item_locations
                    (
                        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
                        item_id integer NOT NULL,
                        room_id integer NOT NULL,
                        side integer NOT NULL,
                        target_user character varying(255) COLLATE pg_catalog."default" NOT NULL,
                        CONSTRAINT active_item_locations_pkey PRIMARY KEY (id),
                        CONSTRAINT ail_item_fkey FOREIGN KEY (item_id)
                            REFERENCES pb.data_items (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
                        CONSTRAINT ail_room_fkey FOREIGN KEY (room_id)
                            REFERENCES pb.data_rooms (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
                        CONSTRAINT ail_user_fkey FOREIGN KEY (target_user)
                            REFERENCES pb.active_users (username) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.active_item_locations
                        OWNER to current_user;""",
            SQL_CREATE_TABLE_START_LOCATION = """
                    CREATE TABLE IF NOT EXISTS pb.start_location
                    (
                        id integer NOT NULL,
                        CONSTRAINT start_location_pkey PRIMARY KEY (id),
                        CONSTRAINT sl_id FOREIGN KEY (id)
                            REFERENCES pb.data_rooms (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )

                    TABLESPACE pg_default;

                    ALTER TABLE IF EXISTS pb.start_location
                        OWNER to current_user;""",

            SQL_CREATE_VIEW_USER_HISTORY = """
                    CREATE OR REPLACE VIEW pb.user_history
                     AS
                     SELECT history.message
                       FROM pb.history
                      WHERE history.target_user::::text = (( SELECT USER AS "user"))
                      ORDER BY history.id DESC
                     LIMIT 20;

                    ALTER TABLE pb.user_history
                        OWNER TO current_user;

                    """,


            SQL_CREATE_PROC_ACTIONS = """
                    CREATE OR REPLACE PROCEDURE pb.actions(
                    \t)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        actions character varying[];
                        a character varying;
                    begin
                        for a in select key from pb.data_functions loop
                            actions = array_append(actions, a);
                        end loop;
                        call pb.say('Actions available: ' || array_to_string(actions, ', '));
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_DESPAWN = """
                    CREATE OR REPLACE PROCEDURE pb.despawn(
                    \tp_item_id integer,
                    \tp_room_id integer)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                        delete from pb.active_item_locations
                            where target_user = (select user)
                            and item_id = p_item_id
                            and room_id = p_room_id;
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_GO = """
                    CREATE OR REPLACE PROCEDURE pb.go(
                    \t)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        uname character varying;
                        c_user pb.active_users%rowtype;
                        c_item pb.data_items%rowtype;
                    begin
                        select user into uname;
                        select * into c_user from pb.active_users
                            where username = uname;
                           \s
                        select * into c_item from pb.data_items
                            where id = (
                                select item_id from pb.active_item_locations
                                    where target_user = uname
                                    and room_id = c_user.location
                                    and side = c_user.facing);
                                   \s
                        if c_item.on_go is NULL then
                            call pb.say('You cannot go into ' || c_item.name || '.');
                        else
                            execute (select action from pb.data_actions
                                        where id = c_item.on_go);
                        end if;
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_GRAB = """
                    CREATE OR REPLACE PROCEDURE pb.grab(
                    \targs character varying[])
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        uname character varying;
                        c_user pb.active_users%rowtype;
                        ci_id integer;
                        i_id integer;
                        tmp character varying[];
                        item_name character varying;
                        gr boolean;
                    begin
                        select array_agg(elem) into tmp from unnest(args) elem
                            where elem <> args[1];
                           \s
                        item_name = array_to_string(tmp, ' ');
                       \s
                        if item_name is NULL then
                            call pb.say('What are you grabbing?');
                            return;
                        end if;

                        select user into uname;
                        select * into c_user from pb.active_users
                            where username = uname;
                           \s
                        select id, grabable into ci_id, gr from pb.data_items
                            where name like '%' || item_name;
                           \s
                        if not gr then
                            call pb.say('You cannot grab ' || item_name || '.');
                        else
                          select item_id into i_id from pb.active_item_locations
                              where target_user = uname
                              and room_id = c_user.location
                              and side = c_user.facing;

                          if i_id = ci_id then
                              call pb.inv_add(i_id);
                              delete from pb.active_item_locations
                                where target_user = uname
                                and room_id = c_user.location
                                and side = c_user.facing;
                              call pb.say('Grabbed ' || item_name || '.');
                          else
                            call pb.say('There is no ' || item_name || ' in here.');
                          end if;
                        end if;
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_INV = """
                    CREATE OR REPLACE PROCEDURE pb.inv(
                    \t)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        uname character varying;
                        i record;
                        v_items character varying[];
                    begin
                        select user into uname;
                       \s
                        for i in select * from pb.active_inventory where target_user = uname loop
                            v_items = array_append(v_items, (select name from pb.data_items where id = i.item_id));
                        end loop;
                       \s
                        call pb.say('Inventory: ' || coalesce(array_to_string(v_items, ', '), 'empty'));
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_INV_ADD = """
                    CREATE OR REPLACE PROCEDURE pb.inv_add(
                    \titem_id integer)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                        insert into pb.active_inventory(target_user, item_id)
                        values(
                            (select user),
                            item_id
                        );
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_INV_RM = """
                    CREATE OR REPLACE PROCEDURE pb.inv_rm(
                    \tp_item_id integer)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                        delete from pb.active_inventory
                        where target_user = (select user)
                        and item_id = p_item_id;
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_LOOK = """
                    CREATE OR REPLACE PROCEDURE pb.look(
                    \t)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        curr_user character varying;
                        c_user pb.active_users%rowtype;
                        roomname character varying;
                        roomsides integer;
                        itemname character varying;
                        msg character varying;
                    begin
                        select user into curr_user;
                       \s
                        select * into c_user from pb.active_users
                            where username = curr_user;
                           \s
                        select name, sides into roomname, roomsides from pb.data_rooms
                            where id = c_user.location;
                       \s
                        select name into itemname from pb.data_items
                            where id in (
                                select item_id from pb.active_item_locations
                                where target_user = curr_user
                                and room_id = c_user.location
                                and side = c_user.facing
                            );
                           \s
                        msg = 'I am in ' || roomname || ' it has ' || roomsides || ' sides. ' ||
                              'I am facing side ' || c_user.facing || ' and there is ' ||
                              coalesce(itemname, 'nothing') || ' in front of me.';
                       \s
                        insert into pb.history(auto_generated, target_user, message)
                            values(true, curr_user, msg);
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_MOVE = """
                    CREATE OR REPLACE PROCEDURE pb.move(
                    \troom_id integer)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                        update pb.active_users set
                            location = room_id,
                            facing = 1
                        where username = (select user);
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_SAY = """
                    CREATE OR REPLACE PROCEDURE pb.say(
                    \tmsg character varying)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                    insert into pb.history(target_user, message) values((select user), msg);
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_SPAWN = """
                    CREATE OR REPLACE PROCEDURE pb.spawn(
                    \titem_id integer,
                    \troom_id integer,
                    \tside integer)
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    begin
                        insert into pb.active_item_locations(item_id, room_id, side, target_user)
                            values(item_id, room_id, side, (select user));
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_TURN = """
                    CREATE OR REPLACE PROCEDURE pb.turn(
                    \targs character varying[])
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        uname character varying;
                        side integer;
                        a_sides integer;
                    begin
                        side = args[2] :::: integer;
                        if side is NULL then
                            call pb.say('What side are you turning?');
                            return;
                        end if;
                       \s
                        select user into uname;
                        select sides into a_sides from pb.data_rooms
                            where id = (select location from pb.active_users where username = uname);
                           \s
                        if side < 1 or side > a_sides then
                            call pb.say('This room has only ' || a_sides || ' sides.');
                        else
                            update pb.active_users set
                                facing = side
                                where username = uname;
                            call pb.look();
                        end if;
                    end;
                    $BODY$;
                    """,
            SQL_CREATE_PROC_USE = """
                    CREATE OR REPLACE PROCEDURE pb.use(
                    \targs character varying[])
                    LANGUAGE 'plpgsql'
                    AS $BODY$
                    declare
                        uname character varying;
                        c_user pb.active_users%rowtype;
                        tmp character varying[];
                        item_name character varying;
                        i_item pb.data_items%rowtype;
                    begin
                        select array_agg(elem) into tmp from unnest(args) elem
                            where elem <> args[1];
                           \s
                        item_name = array_to_string(tmp, ' ');
                       \s
                        if item_name is NULL then
                            call pb.say('What item are you using?');
                            return;
                        end if;

                        select user into uname;
                        select * into c_user from pb.active_users
                            where username = uname;
                           \s
                        select * into i_item from pb.data_items
                            where name like '%' || item_name
                            and id in (
                              select item_id from pb.active_inventory where target_user = uname)
                            limit 1;
                           \s
                        if found then
                            if i_item.on_use is NULL then
                              call pb.say('You cannot use ' || item_name || '.');
                            elseif i_item.use_target is NULL then
                                execute (select action from pb.data_actions
                                    where id = i_item.on_use);
                            else
                                if i_item.use_target in (
                                    select item_id from pb.active_item_locations
                                        where target_user = uname
                                        and room_id = c_user.location
                                        and side = c_user.facing
                                ) then
                                    execute (select action from pb.data_actions
                                                where id = i_item.on_use);
                                else
                                    call pb.say('There is nothing to use ' || item_name || ' on.');
                                end if;
                            end if;
                        else
                            call pb.say('There is no ' || item_name || ' in your inventory.');
                        end if;
                    end;
                    $BODY$;
                    """,


            SQL_CREATE_FUNC_ACT = """
                    CREATE OR REPLACE FUNCTION pb.act(
                    \tcommand character varying)
                        RETURNS SETOF character varying\s
                        LANGUAGE 'plpgsql'
                        COST 100
                        VOLATILE PARALLEL UNSAFE
                        ROWS 1000

                    AS $BODY$
                    begin
                        insert into pb.history(auto_generated, target_user, message)\s
                        values(false, (select user), command);
                        return query (select * from pb.user_history);
                    end;
                    $BODY$;

                    ALTER FUNCTION pb.act(character varying)
                        OWNER TO current_user;

                    GRANT EXECUTE ON FUNCTION pb.act(character varying) TO PUBLIC;

                    GRANT EXECUTE ON FUNCTION pb.act(character varying) TO current_user;

                    """,
            SQL_CREATE_FUNC_START = """
                    CREATE OR REPLACE FUNCTION pb.start(
                    \t)
                        RETURNS SETOF character varying\s
                        LANGUAGE 'plpgsql'
                        COST 100
                        VOLATILE PARALLEL UNSAFE
                        ROWS 1000

                    AS $BODY$
                    declare
                        curr_user character varying;
                        start_location integer;
                        v record;
                    begin
                        select user into curr_user;
                        select id into start_location from pb.start_location;
                       \s
                        delete from pb.history where target_user = curr_user;
                        delete from pb.active_item_locations where target_user = curr_user;
                        delete from pb.active_inventory where target_user = curr_user;
                       \s
                        perform * from pb.active_users where username = curr_user;
                        if found then
                            update pb.active_users set
                                location = start_location,
                                facing = 1
                            where username = curr_user;
                        else
                            insert into pb.active_users values(curr_user, start_location, 1);
                        end if;
                       \s
                        for v in select * from pb.data_item_locations
                        loop
                          insert into pb.active_item_locations(item_id, room_id, side, target_user)
                          values(v.item_id, v.room_id, v.side, curr_user);
                        end loop;
                       \s
                        execute 'call pb.look()';
                       \s
                        return query (select * from pb.user_history);
                    end;
                    $BODY$;

                    ALTER FUNCTION pb.start()
                        OWNER TO current_user;

                    GRANT EXECUTE ON FUNCTION pb.start() TO PUBLIC;

                    GRANT EXECUTE ON FUNCTION pb.start() TO current_user;

                    """;
    @FXML private Label label;
    @FXML private Button button;
    @FXML private Button bReload;
    @FXML private Button bDrop;

    private boolean loaded;

    @FXML
    public void reload(){
        loaded = false;
        loadEngine();
    }

    @FXML
    public void dropSchema(){
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery(SQL_DROP_SCHEMA_PB).executeUpdate();
        em.getTransaction().commit();
        em.close();

        DatabaseGameMaker.emf.close();
        DatabaseGameMaker.emf = null;
        DatabaseGameMaker.instance.setScene("login");
    }

    @FXML
    public void loadEngine(){
        if(!loaded){
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            em.getTransaction().begin();
            try {
                em.createNativeQuery(SQL_DROP_SCHEMA_PB).executeUpdate();
                em.createNativeQuery(SQL_CREATE_SCHEMA_PB).executeUpdate();

                em.createNativeQuery(SQL_CREATE_TABLE_HISTORY).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ACTIONS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ROOMS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ITEMS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ITEM_LOCATIONS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_FUNCTIONS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ACTIVE_USERS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ACTIVE_INVENTORY).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_ACTIVE_ITEM_LOCATIONS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_TABLE_START_LOCATION).executeUpdate();

                em.createNativeQuery(SQL_CREATE_VIEW_USER_HISTORY).executeUpdate();

                em.createNativeQuery(SQL_CREATE_PROC_ACTIONS).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_DESPAWN).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_GO).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_GRAB).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_INV).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_INV_ADD).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_INV_RM).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_LOOK).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_MOVE).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_SAY).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_SPAWN).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_TURN).executeUpdate();
                em.createNativeQuery(SQL_CREATE_PROC_USE).executeUpdate();

                em.createNativeQuery(SQL_CREATE_FUNC_ACT).executeUpdate();
                em.createNativeQuery(SQL_CREATE_FUNC_START).executeUpdate();

                em.createNativeQuery(SQL_CREATE_HISTORY_TRIGGER_FUNC).executeUpdate();
                em.createNativeQuery(SQL_CREATE_HISTORY_TRIGGER).executeUpdate();

                RoomEntity r = new RoomEntity();
                r.setName("the first room");
                r.setSides(4);
                em.persist(r);

                em.createNativeQuery("insert into pb.start_location(id) values((select id from pb.data_rooms limit 1));").executeUpdate();

                em.createNativeQuery("insert into pb.data_functions values " +
                        "('HELP', 'call pb.actions();')," +
                        "('ACTIONS', 'call pb.actions();')," +
                        "('GO', 'call pb.go();')," +
                        "('GRAB', 'call pb.grab($1);')," +
                        "('INVENTORY', 'call pb.inv();')," +
                        "('LOOK', 'call pb.look();')," +
                        "('TURN', 'call pb.turn($1);')," +
                        "('USE', 'call pb.use($1);')").executeUpdate();

                em.getTransaction().commit();

                loaded = true;
            }catch (Exception e){
                em.getTransaction().rollback();

                label.setText("Game engine dump failed: " + e.getMessage());
            }finally {
                em.close();
            }
        }
        if(loaded)
            DatabaseGameMaker.instance.setScene("rooms");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("");
        button.setText("Upload game engine to database");
        loaded = false;

        BigInteger i, s;

        EntityManager em = DatabaseGameMaker.emf.createEntityManager();

        i = (BigInteger) em.createNativeQuery(SQL_SCHEMA_FIND) .getSingleResult();

        if(i.intValue() != 1){
            s = new BigInteger("0");
        }else {
            s = (BigInteger) em.createNativeQuery(SQL_SCHEMA_TABLES_COUNT).getSingleResult();
        }

        em.close();

        loaded = i.intValue() == 1 && s.intValue() == 10;

        if(loaded){
            button.setText("Load Editor");
            label.setText("Game engine loaded successfully");
        }else{
            label.setText("Game engine not found in the database.");
            bDrop.setVisible(false);
            bReload.setVisible(false);
        }
    }
}
