package pb.databasegamemaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseGameMaker extends Application {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;

    public static DatabaseGameMaker instance;
    public static EntityManagerFactory emf;
    private Stage mainWindow;
    private Map<String, FXMLLoader> registeredScenes;
    private Map<String, Scene> loadedScenes;
    private FXMLLoader currentLoader;

    @Override
    public void start(Stage stage) {
        instance = this;

        registeredScenes = new HashMap<>();
        loadedScenes = new HashMap<>();

        registerScene("login", "loginStage.fxml");
        registerScene("rooms", "roomStage.fxml");
        registerScene("actions", "actionStage.fxml");
        registerScene("items", "itemStage.fxml");
        registerScene("locations", "locationStage.fxml");
        registerScene("loader", "loaderStage.fxml");
        registerScene("play", "playStage.fxml");

        mainWindow = stage;
        mainWindow.setTitle("Database Game Maker");
        mainWindow.setWidth(WIDTH);
        mainWindow.setHeight(HEIGHT);
        mainWindow.show();

        setScene("login");
    }

    public void registerScene(String name, String fxmlFile){
        if(registeredScenes.containsKey(name)){
            System.out.println("trying to override a registered scene");
        }else if(!fxmlFile.endsWith(".fxml")){
            System.out.println("trying to load a file that is not a .fxml file");
        }else{
            registeredScenes.put(name, new FXMLLoader(getClass().getResource(fxmlFile)));
        }
    }

    public void setScene(String name) {
        if(!loadedScenes.containsKey(name)){
            if(!registeredScenes.containsKey(name)){
                System.out.println("trying to load an unregistered scene");
                return;
            }else{
                currentLoader = registeredScenes.get(name);
                try {
                    loadedScenes.put(name, new Scene(currentLoader.load(), WIDTH, HEIGHT));
                }catch (IOException ignored){}
            }
        }else {
            currentLoader = registeredScenes.get(name);
            ((Initializable) currentLoader.getController()).initialize(null, null);
        }
        mainWindow.setScene(loadedScenes.get(name));
    }

    public void stop(){
        if(emf != null)
            emf.close();
        mainWindow.close();
    }

    public <T> T getCurrentController(){
        return currentLoader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
